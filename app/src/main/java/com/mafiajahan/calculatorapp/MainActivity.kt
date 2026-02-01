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
                    // Prevent multiple decimal points
                    if (value == "." && currentInput.contains(".")) {
                        return@setOnClickListener
                    }
                    currentInput += value
                    updateDisplay()
//                    binding.inputNumber.text = currentInput
                }
            }

            setOperator(btnPlus, "+")
            setOperator(btnMinus, "-")
            setOperator(btnMultiply, "*")
            setOperator(btnDivide, "/")

            btnEqual.setOnClickListener {
                // Check if we have both numbers and an operator
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

                // Show the complete calculation before showing result
                updateDisplay()
                binding.result.text = formatNumber(resultValue)

                // Reset for next calculation
                // The result becomes the first number for next operation
                firstNumber = if (resultValue.isNaN()) 0.0 else resultValue
                currentInput = ""
                operator = ""

                // After equals, show the result as current input
                //binding.inputNumber.text = formatNumber(firstNumber)

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
                    // Remove the last character
                    currentInput = currentInput.substring(0, currentInput.length - 1)
                    updateDisplay()
                } else if (operator.isNotEmpty()) {
                    // If currentInput is empty, allow deleting the operator
                    operator = ""
                    // Move the firstNumber back to currentInput to allow editing it
                    currentInput = formatNumber(firstNumber)
                    firstNumber = 0.0
                    updateDisplay()
                }
            }

        }
    }

//    private fun updateDisplay() {
//        val firstNumStr = if (firstNumber % 1.0 == 0.0) firstNumber.toLong().toString() else firstNumber.toString()
//
//        if (operator.isEmpty()) {
//            binding.inputNumber.text = currentInput.ifEmpty { "0" }
//        } else {
//            binding.inputNumber.text = "$firstNumStr $operator $currentInput"
//        }
//    }
//
//    private fun  setOperator(buttonId: AppCompatButton, operator: String){
//        buttonId.setOnClickListener {
//            if (currentInput.isNotEmpty()) {
//                firstNumber = currentInput.toDouble()
//                currentInput = ""
//                this.operator = operator
//                //binding.inputNumber.text = operator
//                updateDisplay()
//            }else if (operator.isNotEmpty()) {
//                // Allow changing operator if one is already selected
//                this.operator = operator
//                updateDisplay()
//            }
//    }
//    }
private fun updateDisplay() {
    val firstNumStr = formatNumber(firstNumber)

    if (operator.isEmpty()) {
        // No operator selected, show current input or 0
        binding.inputNumber.text = currentInput.ifEmpty {
            if (firstNumber != 0.0) firstNumStr else "0"
        }
    } else {
        // Operator selected, show full expression
        val secondNumStr = currentInput.ifEmpty { "" }
        binding.inputNumber.text = "$firstNumStr $operator $secondNumStr"
    }
}

    private fun setOperator(buttonId: AppCompatButton, newOperator: String) {
        buttonId.setOnClickListener {
            // If we have current input, set it as first number
            if (currentInput.isNotEmpty()) {
                firstNumber = currentInput.toDouble()
                currentInput = ""
                operator = newOperator
            }
            // If we already have a first number but no current input (e.g., after equals)
            else if (firstNumber != 0.0 && currentInput.isEmpty() && operator.isEmpty()) {
                operator = newOperator
            }
            // If we already have an operator, allow changing it
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
            // Remove trailing zeros
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }

}