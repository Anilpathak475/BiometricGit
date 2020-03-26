package com.anilpathak475.staxter.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.anilpathak475.staxter.R
import com.anilpathak475.staxter.base.BaseFragment
import com.anilpathak475.staxter.data.model.login.ErrorType
import com.anilpathak475.staxter.data.model.login.LoginFormState
import com.anilpathak475.staxter.data.model.login.LoginResult
import com.anilpathak475.staxter.extension.openSecuritySettings
import com.anilpathak475.staxter.extension.showToast
import com.anilpathak475.staxter.store.security.BiometricErrorType
import com.anilpathak475.staxter.store.security.BiometricState
import com.anilpathak475.staxter.viewmodel.LoginScreenViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_screen_fragment.*
import java.util.concurrent.Executor
import javax.inject.Inject


class LoginScreenFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var loginViewModel: LoginScreenViewModel

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_screen_fragment, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        tryToUseBiometric()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginResult.Loading -> handleLoading()
                is LoginResult.Success -> handleSuccess(state.loginFormState)
                is LoginResult.Error -> handleError(state.loginFormState)
            }
        })

        loginViewModel.biometricEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { biometricState ->
                when (biometricState) {
                    BiometricState.READY_TO_USE -> showBiometricOrClearDataIfNewExist()
                    BiometricState.USER_REGISTERED -> showBiometricOrClearDataIfNewExist()
                    BiometricState.USER_NOT_REGISTERED -> showNotRegisteredUserMessage()
                    BiometricState.BIOMETRIC_ERROR -> showBiometricErrorMessage()
                    BiometricState.BIOMETRIC_NOT_SETUP -> setUpBiometric()
                }
            }
        })

        loginViewModel.isBiometricStillAvailable.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { biometricCryptoObject ->
                if (biometricCryptoObject.biometricCryptoObject != null) {
                    biometricLogin(biometricCryptoObject.biometricCryptoObject!!)
                } else {
                    when (biometricCryptoObject.biometricErrorType) {
                        BiometricErrorType.KEY_PERMANENTLY_INVALIDATED -> {
                            username.setText("")
                            username.isEnabled = true
                            loginViewModel.clearAllData()
                            activityGeneralMessagesUtils.showSnackBarWithCloseButton(getString(R.string.new_biometric))
                        }
                        BiometricErrorType.OTHER_BIOMETRIC_ERROR -> showBiometricErrorMessage()
                    }
                }
            }
        })

        loginViewModel.getRegisteredUser()

        login.setOnClickListener {
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        biometric_login.setOnClickListener {
            checkIfBiometricIsAvailable()
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.logoutUser()
    }

    private fun showBiometricErrorMessage() {
        activity?.let { getString(R.string.biometric_error).showToast(it) }
    }

    private fun showNotRegisteredUserMessage() {
        activity?.let { getString(R.string.no_registered_user).showToast(it) }
    }

    private fun setUpBiometric() {
        Snackbar.make(container, R.string.sign_up_snack_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.sign_up_snack_action) { context?.openSecuritySettings() }
            .show()
    }

    private fun handleError(loginFormState: LoginFormState?) {
        loading.visibility = View.GONE

        loginFormState?.errorType?.let {
            val internalErrorMessage = convertToMessage(it)
            activity?.let { it1 -> internalErrorMessage.showToast(it1) }
        }

        loginFormState?.emailError?.let {
            username.error = getString(it)
        }

        loginFormState?.passwordError?.let {
            password.error = getString(it)
        }
    }

    private fun convertToMessage(error: ErrorType): String {
        return when (error) {
            ErrorType.APP_INTERNAL -> getString(R.string.error_other)
            ErrorType.APP_OTHER -> getString(R.string.error_other)
            ErrorType.SERVER_CONNECTION -> getString(R.string.error_connection)
            ErrorType.SERVER_INTERNAL -> getString(R.string.error_server)
            ErrorType.SERVER_TIMEOUT -> getString(R.string.error_limit)
            ErrorType.SERVER_OTHER -> getString(R.string.error_other)
        }
    }

    private fun handleSuccess(loginFormState: LoginFormState?) {
        loading.visibility = View.GONE
        loginFormState?.let {
            if (!it.userEmail.isNullOrEmpty()) {
                username.setText(it.userEmail)
                username.isEnabled = false
                return
            }
        }
        username.isEnabled = true
        navigateToRepoList()
    }

    private fun navigateToRepoList() {
        mainViewModel.logInUser()
        val repoList =
            LoginScreenFragmentDirections.actionLoginScreenToGithubReposScreen()
        findNavController().navigate(repoList)
    }

    private fun handleLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun biometricLogin(biometricCryptoObject: BiometricPrompt.CryptoObject) {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        activity,
                        getString(R.string.authentication_error) + "$errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    navigateToRepoList()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    activity?.let { getString(R.string.authentication_failed).showToast(it) }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_login_staxter))
            .setSubtitle(getString(R.string.login_using_biometric))
            .setNegativeButtonText(getString(R.string.use_account_password))
            .build()

        biometricPrompt.authenticate(promptInfo, biometricCryptoObject)
    }

    private fun checkIfBiometricIsAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.application?.let {
                BiometricManager.from(it).let { biometricManager ->
                    when (biometricManager.canAuthenticate()) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            loginViewModel.tryToUseBiometric()
                        }
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                            showBiometricHardwareErrorMessage()
                        }
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            showBiometricHardwareErrorMessage()
                        }
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                            setUpBiometric()
                        }
                        else -> {
                            showBiometricErrorMessage()
                        }
                    }
                }
            }
        } else {
            activity?.let { getString(R.string.biometric_not_available_on_device).showToast(it) }
        }
    }

    private fun showBiometricHardwareErrorMessage() {
        activity?.let { getString(R.string.biometric_hardware_error).showToast(it) }
    }

    private fun tryToUseBiometric() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.application?.let {
                val biometricManager = BiometricManager.from(it)
                when (biometricManager.canAuthenticate()) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        loginViewModel.tryToUseBiometric()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun showBiometricOrClearDataIfNewExist() {
        loginViewModel.isNewBiometric()
    }

}
