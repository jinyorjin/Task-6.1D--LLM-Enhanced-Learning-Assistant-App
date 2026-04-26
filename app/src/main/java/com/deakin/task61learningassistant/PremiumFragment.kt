package com.deakin.task61learningassistant

import android.content.Context
import android.app.AlertDialog
import android.text.InputType
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class PremiumFragment : Fragment() {

    // Temporary debug helper: call manually while testing to reset to Free.
    private fun resetPremiumForDebug() {
        val premiumKey = getPremiumKey()
        requireContext()
            .getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove(premiumKey)
            .apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_premium, container, false)
        val tvCurrentPlan = view.findViewById<TextView>(R.id.tvCurrentPlan)
        val btnPurchaseStarter = view.findViewById<Button>(R.id.btnPurchaseStarter)
        val btnPurchaseIntermediate = view.findViewById<Button>(R.id.btnPurchaseIntermediate)
        val btnPurchaseAdvanced = view.findViewById<Button>(R.id.btnPurchaseAdvanced)
        val btnBack = view.findViewById<Button>(R.id.btnBackFromPremium)

        updatePlanUi(tvCurrentPlan, btnPurchaseStarter, btnPurchaseIntermediate, btnPurchaseAdvanced)

        val purchaseClick = {
            val premiumKey = getPremiumKey()
            val isPremium = requireContext()
                .getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)
                .getBoolean(premiumKey, false)
            if (isPremium) {
                Toast.makeText(requireContext(), "You already have Premium access", Toast.LENGTH_SHORT).show()
            } else {
                showDemoPaymentDialog(
                    premiumKey = premiumKey,
                    tvCurrentPlan = tvCurrentPlan,
                    btnStarter = btnPurchaseStarter,
                    btnIntermediate = btnPurchaseIntermediate,
                    btnAdvanced = btnPurchaseAdvanced
                )
            }
        }
        btnPurchaseStarter.setOnClickListener { purchaseClick() }
        btnPurchaseIntermediate.setOnClickListener { purchaseClick() }
        btnPurchaseAdvanced.setOnClickListener { purchaseClick() }

        // Temporary debug-only shortcut: long-press Back to reset premium state.
        btnBack.setOnLongClickListener {
            resetPremiumForDebug()
            updatePlanUi(tvCurrentPlan, btnPurchaseStarter, btnPurchaseIntermediate, btnPurchaseAdvanced)
            Toast.makeText(requireContext(), "Premium reset for testing", Toast.LENGTH_SHORT).show()
            true
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun updatePlanUi(
        tvCurrentPlan: TextView,
        btnStarter: Button,
        btnIntermediate: Button,
        btnAdvanced: Button
    ) {
        val premiumKey = getPremiumKey()
        val isPremium = requireContext()
            .getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)
            .getBoolean(premiumKey, false)

        if (isPremium) {
            tvCurrentPlan.text = "Current Plan: Premium"
            btnStarter.text = "Premium Active"
            btnIntermediate.text = "Premium Active"
            btnAdvanced.text = "Premium Active"
        } else {
            tvCurrentPlan.text = "Current Plan: Free"
            btnStarter.text = "Purchase"
            btnIntermediate.text = "Purchase"
            btnAdvanced.text = "Purchase"
        }
    }

    private fun getPremiumKey(): String {
        val username = requireContext()
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            .getString("username", "")
            ?.trim()
            .orEmpty()
        return "is_premium_$username"
    }

    private fun showDemoPaymentDialog(
        premiumKey: String,
        tvCurrentPlan: TextView,
        btnStarter: Button,
        btnIntermediate: Button,
        btnAdvanced: Button
    ) {
        val context = requireContext()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding / 2, padding, 0)
        }

        val notice = TextView(context).apply {
            text = "This is a simulated payment. No real card payment will be processed."
        }
        val etCardholder = EditText(context).apply { hint = "Cardholder name" }
        val etCardNumber = EditText(context).apply {
            hint = "Card number"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val etExpiry = EditText(context).apply {
            hint = "MM/YY"
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        val etCvv = EditText(context).apply {
            hint = "CVV"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        container.addView(notice)
        container.addView(etCardholder)
        container.addView(etCardNumber)
        container.addView(etExpiry)
        container.addView(etCvv)

        AlertDialog.Builder(context)
            .setTitle("Demo Payment")
            .setView(container)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Confirm", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val cardholder = etCardholder.text.toString().trim()
                        val cardNumberDigits = etCardNumber.text.toString().replace(" ", "").trim()
                        val expiry = etExpiry.text.toString().trim()
                        val cvv = etCvv.text.toString().trim()

                        val isValid = cardholder.isNotEmpty() &&
                            cardNumberDigits.length >= 12 &&
                            expiry.isNotEmpty() &&
                            cvv.length >= 3

                        if (!isValid) {
                            Toast.makeText(context, "Please enter valid demo payment details", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        context
                            .getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(premiumKey, true)
                            .apply()

                        updatePlanUi(tvCurrentPlan, btnStarter, btnIntermediate, btnAdvanced)
                        Toast.makeText(context, "Demo Payment Successful - Premium Activated", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
    }
}
