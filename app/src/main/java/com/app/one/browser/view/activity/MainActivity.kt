package com.app.one.browser.view.activity

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.app.one.browser.R
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.databinding.ActivityMainBinding
import com.app.one.browser.extension.*
import com.app.one.browser.view.dialog.SettingDialog
import com.app.one.browser.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    var currentType = ""
    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    when (result.data?.getStringExtra("type").toString()) {
                        "add" -> {
                            showToast("Bookmarks cannot currently be added!")
                        }
                        "bookmark" -> {
                            startActivity(Intent(context, BookMarkActivity::class.java))
                        }
                        "history" -> {
                            startActivity(Intent(context, HistoryActivity::class.java))
                        }
                    }
                }
            }
        }

    override fun onNewCreate() {
        AdManager.loadAd(AdManager.loadingAdmob, context)

        binding.tvTiktok.setOnRepeatClickListener {
            viewModel.startUrl(context, "https://www.tiktok.com/")
        }
        binding.tvInstagram.setOnRepeatClickListener {
            viewModel.startUrl(context, "https://www.instagram.com")
        }
        binding.tvTwitter.setOnRepeatClickListener {
            viewModel.startUrl(context, "https://twitter.com")
        }
        binding.tvVimeo.setOnRepeatClickListener {
            viewModel.startUrl(context, "https://vimeo.com")
        }
        binding.iconSearch.setOnRepeatClickListener {
            Constant.lastUrl = binding.tvSearch.text.toString()
            viewModel.startUrl(context, binding.tvSearch.text.toString())
        }
        binding.tvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.iconSearch.performClick()
            }
            true
        }
        binding.menu.iconDelete.setOnRepeatClickListener {
            startActivity(Intent(context, CleanActivity::class.java))
        }
        binding.menu.iconForward.setOnRepeatClickListener {

        }
        binding.menu.iconReturn.setOnRepeatClickListener {
            when (currentType) {
                "web" -> {
                    startActivity(Intent(context, WebActivity::class.java))
                }
            }
        }
        binding.iconMore.setOnRepeatClickListener {
            launcher.launch(Intent(context, SettingDialog::class.java))
        }
        initBottom()
    }

    private fun initBottom() {
        when (currentType) {
            "web" -> {
                binding.menu.iconForward.setImageResource(R.drawable.icon_main_return_gray)
                binding.menu.iconReturn.setImageResource(R.drawable.icon_main_return)
            }
            else -> {
                binding.menu.iconForward.setImageResource(R.drawable.icon_main_forward_gray)
                binding.menu.iconReturn.setImageResource(R.drawable.icon_main_return_gray)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AdManager.loadAd(AdManager.loadingAdmob, context)
        CoroutineScope(Dispatchers.IO).launch {
            delay(200L)
            AdManager.nativeAdmob.currentJob?.join()
            CoroutineScope(Dispatchers.Main).launch {
                AdManager.showNativeAd(binding.fragmentAd, AdManager.nativeAdmob)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        currentType = intent?.getStringExtra("source").toString()
        initBottom()
    }
}