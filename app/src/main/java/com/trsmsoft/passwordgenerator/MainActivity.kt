package com.trsmsoft.passwordgenerator


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trsmsoft.passwordgenerator.databinding.ActivityMainBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val chars = "abcdefghjklmnpABCDEFGHJKLMNPQRSTUVXWYZqrstuvxwyz"
    private val nums = "1234567890"
    private val symbols = "[]%$#?!@&*()"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnGenerate.setOnClickListener {
            generatePass()
        }

        binding.btnCopy.setOnClickListener {
            copyToClipboard(binding.textPass.text.toString())
        }

        binding.addBtn.setOnClickListener {
            binding.addBtn.isEnabled = false
            showPasswordDialog(this) {
                // Lógica de senha correta
            }
            binding.addBtn.postDelayed({ binding.addBtn.isEnabled = true }, 1000)
        }
    }

    private fun saveMasterPassword(password: String, hint: String) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("master_password", password)
            putString("password_hint", hint)
            apply()
        }
    }

    private fun getMasterPassword(): String? {
        return getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .getString("master_password", null)
    }

    private fun getPasswordHint(): String? {
        return getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .getString("password_hint", null)
    }

    private fun resetMasterPassword() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("master_password")
            remove("password_hint")
            apply()
        }
    }

    private fun showResetPasswordDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_reset_password, null)
        val oldPasswordEditText = dialogView.findViewById<EditText>(R.id.oldPasswordEditText)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)
        val newHintEditText = dialogView.findViewById<EditText>(R.id.newHintEditText)

        MaterialAlertDialogBuilder(context)
            .setTitle("Redefinir Senha Mestre")
            .setMessage("Por favor, insira sua nova senha e uma nova dica.")
            .setView(dialogView)
            .setPositiveButton("Confirmar") { _, _ ->
                val oldPassword = oldPasswordEditText.text.toString()
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()
                val newHint = newHintEditText.text.toString()

                if (oldPassword == getMasterPassword()) {
                    if (newPassword == confirmPassword) {
                        saveMasterPassword(newPassword, newHint)
                        Toast.makeText(context, "Senha mestre atualizada com sucesso.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "As novas senhas não coincidem. Tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Senha atual incorreta. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showPasswordHint(context: Context) {
        val hint = getPasswordHint()
        if (!hint.isNullOrEmpty()) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Dica de Senha")
                .setMessage("A Dica de senha é: $hint")
                .setPositiveButton("OK") { _, _ ->
                    showResetPasswordDialog(context)
                }
                .show()
        } else {
            Toast.makeText(context, "Nenhuma dica de senha disponível.", Toast.LENGTH_SHORT).show()
            showResetPasswordDialog(context)
        }
    }

    private fun showPasswordDialog(context: Context, onPasswordCorrect: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
        val hintEditText = dialogView.findViewById<EditText>(R.id.hintEditText)

        val storedPassword = getMasterPassword()
        val title = if (storedPassword == null) "Crie uma senha mestre" else "Digite a senha mestre"
        val message = if (storedPassword == null) "Por favor, crie uma senha mestre para o aplicativo." else "Por favor, insira a senha mestre para continuar."

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(dialogView)
            .setPositiveButton("Confirmar") { _, _ ->
                val enteredPassword = passwordEditText.text.toString()
                val hint = hintEditText.text.toString()

                if (storedPassword == null) {
                    saveMasterPassword(enteredPassword, hint)
                    Toast.makeText(context, "Senha mestre criada com sucesso.", Toast.LENGTH_SHORT).show()
                    onPasswordCorrect()
                } else {
                    if (enteredPassword == storedPassword) {
                        onPasswordCorrect()
                        val intent = Intent(context, VaultActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Senha incorreta.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)

        if (storedPassword != null) {
            builder.setNeutralButton("Esqueci a senha") { _, _ ->
                showPasswordHint(context)
            }
        }

        builder.show()
    }

    private fun generatePass() {
        val length = binding.textLenght.text.toString().toIntOrNull() ?: run {
            binding.textLenght.error = "Valor inválido"
            return
        }

        if (length > 99) {
            binding.textLenght.error = "Valor não pode ser maior que 99"
            return
        }

        // Conjuntos de caracteres
        val letters = chars
        val numbers = if (binding.chkNum.isChecked) nums else ""
        val specialSymbols = if (binding.chkSymbols.isChecked) symbols else ""

        // Lista para garantir a inclusão de pelo menos um caractere de cada tipo
        val passwordCharacters = mutableListOf<Char>()

        // Garantir pelo menos um número e/ou símbolo, se selecionado
        if (numbers.isNotEmpty()) passwordCharacters.add(numbers.random())
        if (specialSymbols.isNotEmpty()) passwordCharacters.add(specialSymbols.random())

        // Preencher o restante da senha com caracteres aleatórios
        val availableChars = letters + numbers + specialSymbols
        while (passwordCharacters.size < length) {
            passwordCharacters.add(availableChars.random())
        }

        // Embaralhar a lista para evitar que os caracteres garantidos fiquem sempre no início
        passwordCharacters.shuffle()

        // Configurar a senha gerada
        val password = passwordCharacters.joinToString("")
        binding.textPass.setText(password)
    }

    private fun copyToClipboard(text: String) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Password copiado", text)
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this, "Password Copiado!", Toast.LENGTH_SHORT).show()
    }

    private fun enableEdgeToEdge() {
        // Implementação para o modo Edge-to-Edge
    }
}