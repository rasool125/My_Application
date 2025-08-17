package com.example.myapplication.sticker

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.inovativetranslator.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleDialogLoading : DialogFragment() {

    private var mOnDismiss = false

    private var onDialogDismissListener: (() -> Unit)? = null
    private var onDialogShowFail: (() -> Unit)? = null

    override fun show(manager: FragmentManager, tag: String?) {
        val t = "SampleDialogLoading"
        val exitDialog = manager.findFragmentByTag(t)
        if (activity?.isDestroyed == true || activity?.isFinishing == true) {
            onDialogShowFail?.invoke()
            return
        }
        if (exitDialog?.isAdded == true) {
            onDialogShowFail?.invoke()
            return
        }
        if (exitDialog == null) {
            kotlin.runCatching {
                val ft: FragmentTransaction = manager.beginTransaction()
                ft.add(this, t)
                ft.commitNow()
            }.onFailure {
                onDialogShowFail?.invoke()
                it.printStackTrace()
            }
        }
    }

    fun show(manager: FragmentManager?, onShowFail: (() -> Unit)? = null) {
        dismissDialog()
        onDialogShowFail = onShowFail
        manager?.let {
            show(it, tag = null)
        } ?: onDialogShowFail?.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            LayoutInflater.from(context).inflate(com.ikame.android.sdk.R.layout.dialog_ads_loading, container, false)
        view?.setOnClickListener {
            closeDialogByClick()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeDialog()
    }

    private fun dismissDialog() {
        kotlin.runCatching {
            dismiss()
            dismissAllowingStateLoss()
        }.onFailure {
            kotlin.runCatching {
                dismissAllowingStateLoss()
            }
        }
    }

    fun closeDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(3000)
            dismissDialog()
        }
    }

    fun closeDialogNow() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            dismissDialog()
        }
    }

    fun closeDialogByClick() {
        dismissDialog()
        lifecycleScope.launch(Dispatchers.Main) {
            if (context == null)
                return@launch
            delay(2000)
            dismissDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val window = dialog?.window
            val windowParams = window?.attributes
            windowParams?.dimAmount = 0f
            dialog?.setCancelable(true)
            dialog?.setCanceledOnTouchOutside(true)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setGravity(Gravity.CENTER)
            window?.attributes = windowParams
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mOnDismiss = true
        onDialogDismissListener?.invoke()

    }

    companion object {
        fun newInstance(): SampleDialogLoading {
            val args = Bundle()
            val fragment = SampleDialogLoading()
            fragment.arguments = args
            return fragment
        }
    }

}