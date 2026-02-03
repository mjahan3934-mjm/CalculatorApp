package com.mafiajahan.calculatorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mafiajahan.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentInput = ""
    private var operator = ""
    private var firstNumber = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        binding.apply {
            val numberButtons = listOf(
                btn0, btn1, btn2, btn3,
                btn4, btn5, btn6, btn7,
                btn8, btn9, btnDoubleZero, btnDot
            )

            for (button in numberButtons) {
                button.setOnClickListener {
                    val value = button.text.toString()
                   
                    if (value == "." && currentInput.contains(".")) {
                        return@setOnClickListener
                    }
                    currentInput += value
                    updateDisplay()

                }
            }

            setOperator(btnPlus, "+")
            setOperator(btnMinus, "-")
            setOperator(btnMultiply, "*")
            setOperator(btnDivide, "/")

            btnEqual.setOnClickListener {
               
                if (currentInput.isEmpty() || operator.isEmpty() || firstNumber == 0.0)
                    return@setOnClickListener

                val secondNumber = currentInput.toDouble()
                val resultValue = when (operator) {
                    "+" -> firstNumber + secondNumber
                    "-" -> firstNumber - secondNumber
                    "*" -> firstNumber * secondNumber
                    "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
                    else -> secondNumber
                }

              
                updateDisplay()
                binding.result.text = formatNumber(resultValue)

                firstNumber = if (resultValue.isNaN()) 0.0 else resultValue
                currentInput = ""
                operator = ""

                

            }

            btnClear.setOnClickListener {
                currentInput = ""
                operator = ""
                firstNumber = 0.0
                inputNumber.text = ""
                result.text = ""
            }

            btnBackspace.setOnClickListener {
                if (currentInput.isNotEmpty()) {
                  
                    currentInput = currentInput.substring(0, currentInput.length - 1)
                    updateDisplay()
                } else if (operator.isNotEmpty()) {
              
                    operator = ""
                    
                    currentInput = formatNumber(firstNumber)
                    firstNumber = 0.0
                    updateDisplay()
                }
            }

        }
    }

private fun updateDisplay() {
    val firstNumStr = formatNumber(firstNumber)

    if (operator.isEmpty()) {
   
        binding.inputNumber.text = currentInput.ifEmpty {
            if (firstNumber != 0.0) firstNumStr else "0"
        }
    } else {
   
        val secondNumStr = currentInput.ifEmpty { "" }
        binding.inputNumber.text = "$firstNumStr $operator $secondNumStr"
    }
}

    private fun setOperator(buttonId: AppCompatButton, newOperator: String) {
        buttonId.setOnClickListener {
         
            if (currentInput.isNotEmpty()) {
                firstNumber = currentInput.toDouble()
                currentInput = ""
                operator = newOperator
            }
            
            else if (firstNumber != 0.0 && currentInput.isEmpty() && operator.isEmpty()) {
                operator = newOperator
            }
        
            else if (operator.isNotEmpty()) {
                operator = newOperator
            }

            updateDisplay()
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }

}
