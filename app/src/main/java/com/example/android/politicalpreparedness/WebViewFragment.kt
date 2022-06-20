package com.example.android.politicalpreparedness

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentWebViewBinding
    private val args: WebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentWebViewBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openWebView(args.url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String) {
        viewDataBinding.webview.settings.javaScriptEnabled = true
        viewDataBinding.webview.loadUrl(url)
    }
}