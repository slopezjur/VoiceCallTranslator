package com.sergiolopez.voicecalltranslator.feature.call.telecom.repository

import android.content.Context
import android.net.Uri
import android.telecom.DisconnectCause
import android.util.Log
import androidx.core.telecom.CallAttributesCompat
import androidx.core.telecom.CallControlResult
import androidx.core.telecom.CallControlScope
import androidx.core.telecom.CallsManager
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCallAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The central repository that keeps track of the current call and allows to register new calls.
 *
 * This class contains the main logic to integrate with Telecom SDK.
 *
 * @see registerCall
 */
@Singleton
class TelecomCallRepository @Inject constructor(
    context: Context
) {
    private var callsManager: CallsManager

    init {
        // Create the Jetpack Telecom entry point
        callsManager = CallsManager(context).apply {
            // Register with the telecom interface with the supported capabilities
            registerAppWithTelecom(
                capabilities = CallsManager.CAPABILITY_SUPPORTS_CALL_STREAMING and
                        CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING,
            )
        }
    }

    // Keeps track of the current TelecomCall state
    private val _currentCall: MutableStateFlow<TelecomCall> = MutableStateFlow(TelecomCall.None)
    val currentCall = _currentCall.asStateFlow()

    /**
     * Register a new call with the provided attributes.
     * Use the [currentCall] StateFlow to receive status updates and process call related actions.
     */
    suspend fun registerCall(call: Call.CallData, isIncoming: Boolean) {
        // For simplicity we don't support multiple calls
        check(_currentCall.value !is TelecomCall.Registered) {
            "There cannot be more than one call at the same time."
        }

        // Create the call attributes
        val attributes = CallAttributesCompat(
            displayName = call.calleeId,
            address = Uri.parse(
                Json.encodeToString(Call.CallData.serializer(), call)
            ),
            direction = if (isIncoming) {
                CallAttributesCompat.DIRECTION_INCOMING
            } else {
                CallAttributesCompat.DIRECTION_OUTGOING
            },
            callType = CallAttributesCompat.CALL_TYPE_AUDIO_CALL,
            callCapabilities = (CallAttributesCompat.SUPPORTS_SET_INACTIVE
                    or CallAttributesCompat.SUPPORTS_STREAM
                    or CallAttributesCompat.SUPPORTS_TRANSFER),
        )

        // Creates a channel to send actions to the call scope.
        val actionSource = Channel<TelecomCallAction>()
        // Register the call and handle actions in the scope
        try {
            callsManager.addCall(
                attributes,
                onIsCallAnswered, // Watch needs to know if it can answer the call
                onIsCallDisconnected,
                onIsCallActive,
                onIsCallInactive
            ) {
                // Consume the actions to interact with the call inside the scope
                launch {
                    processCallActions(actionSource.consumeAsFlow())
                }

                // Update the state to registered with default values while waiting for Telecom updates
                _currentCall.value = TelecomCall.Registered(
                    id = getCallId(),
                    isActive = false,
                    isOnHold = false,
                    callAttributes = attributes,
                    isMuted = false,
                    errorCode = null,
                    currentCallEndpoint = null,
                    availableCallEndpoints = emptyList(),
                    actionSource = actionSource,
                )

                launch {
                    currentCallEndpoint.collect {
                        updateCurrentCall {
                            copy(currentCallEndpoint = it)
                        }
                    }
                }
                launch {
                    availableEndpoints.collect {
                        updateCurrentCall {
                            copy(availableCallEndpoints = it)
                        }
                    }
                }
                launch {
                    isMuted.collect {
                        updateCurrentCall {
                            copy(isMuted = it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Really big error: ", e.toString())
        } finally {
            _currentCall.value = TelecomCall.None
        }
    }

    /**
     * Collect the action source to handle client actions inside the call scope
     */
    private suspend fun CallControlScope.processCallActions(actionSource: Flow<TelecomCallAction>) {
        actionSource.collect { action ->
            when (action) {
                is TelecomCallAction.Answer -> {
                    doAnswer()
                }

                is TelecomCallAction.Disconnect -> {
                    doDisconnect(action)
                }

                is TelecomCallAction.SwitchAudioEndpoint -> {
                    doSwitchEndpoint(action)
                }

                is TelecomCallAction.TransferCall -> {
                    val call = _currentCall.value as? TelecomCall.Registered
                    val endpoints = call?.availableCallEndpoints?.firstOrNull {
                        it.identifier == action.endpointId
                    }
                    requestEndpointChange(
                        endpoint = endpoints ?: return@collect,
                    )
                }

                TelecomCallAction.Hold -> {
                    when (val result = setInactive()) {
                        is CallControlResult.Success -> {
                            onIsCallInactive()
                        }

                        is CallControlResult.Error -> {
                            updateCurrentCall {
                                copy(errorCode = result.errorCode)
                            }
                        }
                    }
                }

                TelecomCallAction.Activate -> {
                    when (val result = setActive()) {
                        is CallControlResult.Success -> {
                            onIsCallActive()
                        }

                        is CallControlResult.Error -> {
                            updateCurrentCall {
                                copy(errorCode = result.errorCode)
                            }
                        }
                    }
                }

                is TelecomCallAction.ToggleMute -> {
                    // We cannot programmatically mute the telecom stack. Instead we just update
                    // the state of the call and this will start/stop audio capturing.
                    updateCurrentCall {
                        copy(isMuted = !isMuted)
                    }
                }
            }
        }
    }

    /**
     * Update the current state of our call applying the transform lambda only if the call is
     * registered. Otherwise keep the current state
     */
    private fun updateCurrentCall(transform: TelecomCall.Registered.() -> TelecomCall) {
        _currentCall.update { call ->
            if (call is TelecomCall.Registered) {
                call.transform()
            } else {
                call
            }
        }
    }

    private suspend fun CallControlScope.doSwitchEndpoint(action: TelecomCallAction.SwitchAudioEndpoint) {
        // TODO once availableCallEndpoints is a state flow we can just get the value
        val endpoints = (_currentCall.value as TelecomCall.Registered).availableCallEndpoints

        // Switch to the given endpoint or fallback to the best possible one.
        val newEndpoint = endpoints.firstOrNull { it.identifier == action.endpointId }

        if (newEndpoint != null) {
            requestEndpointChange(newEndpoint).also {
                Log.d("MPB", "Endpoint ${newEndpoint.name} changed: $it")
            }
        }
    }

    private suspend fun CallControlScope.doDisconnect(action: TelecomCallAction.Disconnect) {
        disconnect(action.cause)
        onIsCallDisconnected(action.cause)
    }

    private suspend fun CallControlScope.doAnswer() {
        when (answer(CallAttributesCompat.CALL_TYPE_AUDIO_CALL)) {
            is CallControlResult.Success -> {
                onIsCallAnswered(CallAttributesCompat.CALL_TYPE_AUDIO_CALL)
            }

            is CallControlResult.Error -> {
                updateCurrentCall {
                    TelecomCall.Unregistered(
                        id = id,
                        callAttributes = callAttributes,
                        disconnectCause = DisconnectCause(DisconnectCause.BUSY),
                    )
                }
            }
        }
    }

    /**
     *  Can the call be successfully answered??
     *  TIP: We would check the connection/call state to see if we can answer a call
     *  Example you may need to wait for another call to hold.
     **/
    val onIsCallAnswered: suspend (type: Int) -> Unit = {
        updateCurrentCall {
            copy(isActive = true, isOnHold = false)
        }
    }

    /**
     * Can the call perform a disconnect
     */
    val onIsCallDisconnected: suspend (cause: DisconnectCause) -> Unit = {
        updateCurrentCall {
            TelecomCall.Unregistered(id, callAttributes, it)
        }
    }

    /**
     *  Check is see if we can make the call active.
     *  Other calls and state might stop us from activating the call
     */
    val onIsCallActive: suspend () -> Unit = {
        updateCurrentCall {
            copy(
                errorCode = null,
                isActive = true,
                isOnHold = false,
            )
        }
    }

    /**
     * Check to see if we can make the call inactivate
     */
    val onIsCallInactive: suspend () -> Unit = {
        updateCurrentCall {
            copy(
                errorCode = null,
                isOnHold = true
            )
        }
    }
}