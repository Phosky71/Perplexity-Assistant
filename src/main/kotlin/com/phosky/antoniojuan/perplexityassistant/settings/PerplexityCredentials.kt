package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

object PerplexityCredentials {
    private const val SERVICE_NAME = "PerplexitySonarProPlugin.ApiKey"
    private fun attributes(): CredentialAttributes =
        CredentialAttributes(SERVICE_NAME)

    fun saveApiKey(apiKey: String?) {
        val attrs = attributes()
        val credentials = if (apiKey.isNullOrBlank()) null else Credentials(null, apiKey)
        PasswordSafe.instance.set(attrs, credentials)
    }

    fun getApiKey(): String? {
        val attrs = attributes()
        val credentials = PasswordSafe.instance.get(attrs)
        return credentials?.getPasswordAsString()
    }
}
