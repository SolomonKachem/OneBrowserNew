package com.app.one.browser.view.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.app.one.browser.R
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.database.BookMark
import com.app.one.browser.database.DBInit
import com.app.one.browser.database.History
import com.app.one.browser.databinding.ActivityWebBinding
import com.app.one.browser.extension.*
import com.app.one.browser.view.dialog.SettingDialog
import com.app.one.browser.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URISyntaxException

class WebActivity:BaseActivity<ActivityWebBinding>(ActivityWebBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private var currentHistory = History()
    var currentUrl:String = ""
    var currentTitle:String = ""
    var currentIcon:String = ""
    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        "back".showSystemLog()
        when(result.resultCode){
            Activity.RESULT_OK ->{
                " ok ".showSystemLog()
                when(result.data?.getStringExtra("type").toString()){
                    "add"->{
                        val currentItem = binding.webView.copyBackForwardList().currentItem
                        if(currentItem!=null){
                            if(currentItem.favicon != null){
                                bitmap2Path(currentItem.favicon!!,viewModel.getCurrentImageUrl()){path->
                                    val bookMark = BookMark(currentItem.url,path,currentItem.title,"","")
                                    CoroutineScope(Dispatchers.IO).launch {
                                        DBInit.db.dao.insertBookMark(bookMark)
                                        showToast("BookMarked!！")
                                    }
                                }
                            }else{
                                val bookMark = BookMark(currentItem.url,"",currentItem.title,"","")
                                CoroutineScope(Dispatchers.IO).launch {
                                    DBInit.db.dao.insertBookMark(bookMark)
                                    showToast("BookMarked!！")
                                }
                            }
                        }else{
                            showToast("Bookmarks cannot currently be added!")
                        }
                    }
                    "bookmark"->{
                        startActivity(Intent(context,BookMarkActivity::class.java))
                    }
                    "history"->{
                        startActivity(Intent(context,HistoryActivity::class.java))
                    }
                }
            }
        }
    }
    override fun onNewCreate() {
        currentUrl = intent.getStringExtra("url").toString()
        viewModel.currentUrl.observe(this){url->
            currentUrl = url
            binding.tvSearch.setText(currentUrl)
            binding.webView.stopLoading()
            loadWebPage(currentUrl)
        }
        binding.iconMore.setOnRepeatClickListener {
            launcher.launch(Intent(context, SettingDialog::class.java))
        }
        binding.iconSearch.setOnRepeatClickListener {
            Constant.lastUrl = binding.tvSearch.text.toString()
            viewModel.searchUrl(binding.tvSearch.text.toString())
            hideKeyboard()
        }
        binding.tvSearch.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                binding.iconSearch.performClick()
            }
            true
        }
        binding.menu.iconDelete.setOnRepeatClickListener {
            startActivity(Intent(context,CleanActivity::class.java))
        }
        binding.menu.iconForward.setOnRepeatClickListener {
            if(binding.webView.canGoForward()){
                binding.webView.goForward()
            }
            initBottomMenu()
        }
        binding.menu.iconReturn.setOnRepeatClickListener {
            if(binding.webView.canGoBack()){
                binding.webView.goBack()
            }else{
                showMainPage()
            }
            initBottomMenu()
        }
        binding.menu.iconHome.setOnRepeatClickListener {
            showMainPage()
        }

        loadWebViewSettingOnResume()
        binding.webView.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress < 100) {
                    binding.progress.progress = newProgress
                    binding.progress.visibility = View.VISIBLE
                }
                super.onProgressChanged(view, newProgress)
            }
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                currentUrl = binding.webView.url.toString()
                //currentIcon = "${currentUrl.domain()}/favicon.ico"
                currentTitle = title
                saveBrowserHistory()
            }
            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
                bitmap2Path(icon,viewModel.getCurrentImageUrl()){path->
                    currentIcon = path
                    saveBrowserHistory()
                }
            }
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)
            }
        }
        binding.webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.isEmpty()) return false
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    binding.webView.loadUrl(url)
                    return true
                } else {
                    try {
                        if (url.startsWith("intent://")) {
                            val intent: Intent
                            try {
                                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                intent.addCategory("android.intent.category.BROWSABLE")
                                intent.component = null
                                intent.selector = null
                                val resolves: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
                                if (resolves.isNotEmpty()) {
                                    startActivity(intent)
                                }
                                return true
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                        }
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        return true
                    } catch (e: Exception) {
                    }
                }
                return false
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(context, "Error: " + error?.description.toString(), Toast.LENGTH_SHORT).show()
                }
                super.onReceivedError(view, request, error)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progress.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progress.visibility = View.INVISIBLE
                super.onPageFinished(view, url)
            }
        }
        binding.webView.isSaveEnabled = true
        binding.webView.setOnKeyListener(object:View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
                        binding.webView.goBack()
                        initBottomMenu()
                        return true
                    }
                }
                return false
            }
        })
        initBottomMenu()
        viewModel.searchUrl(currentUrl)
    }
    private fun showMainPage(){
        startActivity(Intent(context,MainActivity::class.java))
    }
    private fun loadWebViewSettingOnResume(){
        binding.webView.settings.loadsImagesAutomatically = Constant.autoLoadImage
        //binding.webView.settings.blockNetworkImage = Constant.autoLoadImage
        //binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
    }
    fun saveBrowserHistory(){
        if(currentUrl.isNotEmpty() && currentIcon.isNotEmpty() && currentTitle.isNotEmpty()){
            val history = History(currentUrl,currentIcon,currentTitle,"","")
            CoroutineScope(Dispatchers.IO).launch {
                DBInit.db.dao.insertHistory(history)
                currentUrl = ""
                currentIcon = ""
                currentTitle = ""
            }
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
    private fun initBottomMenu(){
        if(binding.webView.canGoForward()){
            binding.menu.iconForward.setImageResource(R.drawable.icon_main_forward)
        }else{
            binding.menu.iconForward.setImageResource(R.drawable.icon_main_forward_gray)
        }
        binding.menu.iconReturn.setImageResource(R.drawable.icon_main_return)
    }

    fun bitmap2Path(bitmap: Bitmap, path: String,block:(String)->Unit): String {
        try {
            val file = File(context.cacheDir,path)
            val os: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
            block(file.absolutePath)
        } catch (e: java.lang.Exception) {
            e.message?.showSystemLog()
        }
        return path
    }
    private fun loadWebPage(query:String){
        val queryOrUrl = query.trim { it <= ' ' }
        val isUrl = Patterns.WEB_URL.matcher(queryOrUrl).matches()
        if (isUrl) {
            var url = Uri.parse(queryOrUrl)
            if (TextUtils.isEmpty(url.scheme)) {
                url = url.buildUpon()
                    .scheme("http")
                    .build()
            }
            binding.webView.loadUrl(url.toString())
            return
        }

        val searchDomainAndQueryParam: Array<String> = getSearchDomainAndQueryParam()
        val searchQuery = Uri.parse(searchDomainAndQueryParam[0])
            .buildUpon()
            .appendQueryParameter(searchDomainAndQueryParam[1], queryOrUrl)
            .build()
        binding.webView.loadUrl(searchQuery.toString())
    }
    private fun getSearchDomainAndQueryParam(): Array<String> {
        return when (Constant.searchEngine) {
            "0" -> {
                arrayOf("https://www.google.com/search","q")
            }
            "1" -> {
                arrayOf("https://www.bing.com/search","q")
            }
            "2" -> {
                arrayOf("https://search.yahoo.com/search","p")
            }
            "3" -> {
                arrayOf("https://duckduckgo.com/","q")
            }
            else -> {
                arrayOf("https://www.youtube.com/results","search_query")
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if(binding.clWeb.visibility == View.VISIBLE) {
            binding.webView.onResume()
        }
        loadWebViewSettingOnResume()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val url = intent?.getStringExtra("url").toString()
        if(url.isNotNull()){
            currentUrl = url
            viewModel.searchUrl(currentUrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            binding.webView.destroy()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}