package com.rajangarg.linkpreviewdemo

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.kedia.ogparser.OpenGraphCallback
import com.kedia.ogparser.OpenGraphParser
import com.kedia.ogparser.OpenGraphResult
import com.rajangarg.linkpreviewdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: LinksListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        setTextChangeListener()
        setEndIconClickListener()
    }

    private fun setEndIconClickListener() {
        binding.tilLink.setEndIconOnClickListener {
            binding.tilLink.error = null

            val text = binding.etLink.text.toString().trim()

            if (text.isNullText())
                return@setEndIconOnClickListener

            if (!text.isValidLink()) {
                binding.tilLink.error = getString(R.string.invalid_url)
                return@setEndIconOnClickListener
            }

            showProgress(true)
            loadLinkPreview(text, ::addLinkPreviewToList)
        }
    }

    private fun initAdapter() {
        adapter = LinksListAdapter()
        binding.rvLinkPreview.adapter = adapter
    }

    private fun setTextChangeListener() {
        var previewURL: String? = null
        val mTimer = object : CountDownTimer(1000L, 100) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                previewURL?.let {
                    loadLinkPreview(it, ::showSmallLinkPreview)
                }
            }
        }

        binding.etLink.doAfterTextChanged {
            mTimer.cancel()
            val url = it.toString().getFirstContainingURL()
            if (url == null)
                binding.layoutLinkPreviewSmall.root.setVisibility(false)
            else {
                previewURL = url
                mTimer.start()
            }
        }
    }

    private fun loadLinkPreview(link: String, showLinkPreview: (OpenGraphResult) -> Unit) {
        val openGraphParser = OpenGraphParser(object : OpenGraphCallback {
            override fun onError(error: String) {
                showProgress(false)
                binding.layoutLinkPreviewSmall.root.setVisibility(false)
            }

            override fun onPostResponse(openGraphResult: OpenGraphResult) {
                showLinkPreview(openGraphResult)
            }
        }, showNullOnEmpty = true, context = this)
        openGraphParser.parse(link)
    }

    private fun showSmallLinkPreview(openGraphResult: OpenGraphResult) {
        binding.layoutLinkPreviewSmall.root.setVisibility(true)
        binding.layoutLinkPreviewSmall.openGraphResult = openGraphResult
    }

    private fun addLinkPreviewToList(openGraphResult: OpenGraphResult) {
        binding.layoutLinkPreviewSmall.root.setVisibility(false)
        binding.tvNoData.setVisibility(false)
        showProgress(false)
        binding.etLink.text = null

        adapter.addLink(openGraphResult)
        binding.rvLinkPreview.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showProgress(toShow: Boolean) {
        binding.tilLink.isEndIconVisible = !toShow
        binding.progress.setVisibility(toShow)
    }

    private fun String?.isValidLink(): Boolean {
        this?.let {
            return Patterns.WEB_URL.matcher(it).matches()
        }
        return false
    }

    private fun String?.getFirstContainingURL(): String? {
        val words = this?.split(" ")
        words?.forEach { word ->
            if (word.isValidLink()) return word
        }
        return null
    }
}