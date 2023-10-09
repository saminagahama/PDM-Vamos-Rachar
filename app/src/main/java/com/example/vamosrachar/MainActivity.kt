package com.example.vamosrachar

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var edtConta: EditText
    private lateinit var edtNumberOfPeople: EditText
    private lateinit var btnCompartilhar: Button
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var calculatedAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtConta = findViewById(R.id.edtConta)
        edtNumberOfPeople = findViewById(R.id.edtNumberOfPeople)
        btnCompartilhar = findViewById(R.id.btnCompartilhar)
        calculatedAmountTextView = findViewById(R.id.calculatedAmountTextView)

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale("pt", "BR")
                val availableLocales = textToSpeech.availableLanguages
                if (availableLocales.contains(locale)) {
                    textToSpeech.language = locale
                } else {
                    val fallbackLocale = Locale.ENGLISH
                    textToSpeech.language = fallbackLocale
                }
            }
        }

        edtConta.addTextChangedListener(textWatcher)
        edtNumberOfPeople.addTextChangedListener(textWatcher)
        btnCompartilhar.setOnClickListener {
            shareCalculatedAmount()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            calculateAndDisplayAmount()
        }
        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun calculateAndDisplayAmount() {
        val billAmountStr = edtConta.text.toString()
        val numberOfPeopleStr = edtNumberOfPeople.text.toString()

        if (billAmountStr.isNotEmpty() && numberOfPeopleStr.isNotEmpty()) {
            val billAmount = billAmountStr.toDoubleOrNull()
            val numberOfPeople = numberOfPeopleStr.toDoubleOrNull()

            if (billAmount != null && numberOfPeople != null && numberOfPeople > 0) {
                val splitAmount = billAmount / numberOfPeople
                val text = "O valor calculado é $splitAmount reais."

                calculatedAmountTextView.text = String.format("%.2f", splitAmount)

                @Suppress("DEPRECATION")
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            } else {
                calculatedAmountTextView.text = ""
                showError("Inválido. Insira um valor válido.")
            }
        } else {
            calculatedAmountTextView.text = "" // Clear the result text
            showError("Por favor, insira o valor da conta e o número de pessoas.")
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun shareCalculatedAmount() {
        val calculatedAmount = calculatedAmountTextView.text.toString()
        val text = "O valor calculado é $calculatedAmount reais."

        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(text)
            .setType("text/plain")
            .intent

        startActivity(shareIntent)
    }
}
