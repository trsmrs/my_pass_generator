package com.example.passwordgenerator

import android.app.AlertDialog
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
import com.example.passwordgenerator.databinding.ActivityMainBinding
import com.example.passwordgenerator.databinding.ActivityVaultBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var passwordLength = 99
    private val chars = "abcdefghjklmnpABCDEFGHJKLMNPQRSTUVXWYZqrstuvxwyz"
    private val chars2 = "123456789012345678901234567890123456789012345678"
    private val chars3 = "?!@&*()[]%$#?!@&*()[]%$#?!@&*()[]%$#?!@&*()[]%$#"
    private var password = ""
    private var buttonActivated = false
    private var buttonSymbols = false
    private var selectedChars = ""
   


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
        val result_text: EditText = findViewById(R.id.text_pass)
        val btn: Button = findViewById(R.id.btn_generate)
        val btnCopy: Button = findViewById(R.id.btn_copy)
        val txt_lenth: EditText = findViewById(R.id.text_lenght)

        val chk_num: CheckBox = findViewById(R.id.chk_num)
        val chk_symbols: CheckBox = findViewById(R.id.chk_symbols)



         fun saveMasterPassword(password: String) {
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("master_password", password)
            editor.apply()
        }

         fun getMasterPassword(): String? {
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            return sharedPreferences.getString("master_password", null)
        }


        fun showPasswordDialog(context: Context, onPasswordCorrect: () -> Unit) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_password, null)
            val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

            val storedPassword = getMasterPassword()
            val title = if (storedPassword == null) "Crie uma senha mestre" else "Digite a senha mestre"
            val message = if (storedPassword == null) "Por favor, crie uma senha mestre para o aplicativo." else "Por favor, insira a senha mestre para continuar."

            val builder = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(dialogView)
                .setPositiveButton("Confirmar") { _, _ ->
                    val enteredPassword = passwordEditText.text.toString()

                    if (storedPassword == null) {
                        // Criando uma nova senha mestre
                        saveMasterPassword(enteredPassword)
                        Toast.makeText(context, "Senha mestre criada com sucesso.", Toast.LENGTH_SHORT).show()
                        onPasswordCorrect()
                    } else {
                        // Verificando a senha existente
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

            builder.show()
        }

        binding.addBtn.setOnClickListener {
            showPasswordDialog(this) {
                // Ação a ser executada quando a senha estiver correta
            }
        }

        fun generatePass() {
            buttonActivated = chk_num.isChecked
            buttonSymbols = chk_symbols.isChecked

            passwordLength = txt_lenth.text.toString().toInt()

            selectedChars = when {
                buttonActivated && buttonSymbols -> chars + chars2 + chars3
                buttonActivated -> chars + chars2
                buttonSymbols -> chars + chars3
                else -> chars
            }
            for (i in 0 until passwordLength) {
                val randomIndex = (Math.random() * selectedChars.length).toInt()
                password += selectedChars.substring(randomIndex, randomIndex + 1)
            }
            result_text.setText(password)
            password = ""
        }

        btn.setOnClickListener {
            if(txt_lenth.text.isEmpty()){
                txt_lenth.setError("Valor não pode ser nulo")
            }
            else if (txt_lenth.text.length >2){
                txt_lenth.setError("Valor não pode ser maior que 99")
            }
            else{
                generatePass()
            }
        }
        btnCopy.setOnClickListener {
            val textToCopy = result_text.text.toString()
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Password copiado", textToCopy)
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this, "Password Copiado!", Toast.LENGTH_SHORT).show()
        }




      }
    }