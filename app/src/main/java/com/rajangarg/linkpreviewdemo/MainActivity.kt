package com.rajangarg.linkpreviewdemo

import android.os.Bundle
import android.util.Patterns
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.kedia.ogparser.OpenGraphCallback
import com.kedia.ogparser.OpenGraphParser
import com.kedia.ogparser.OpenGraphResult
import com.rajangarg.linkpreviewdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: LinksListAdapter
    private var linksList = ArrayList<OpenGraphResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LinksListAdapter()
        binding.rvLinkPreview.adapter = adapter

        binding.tilLink.setEndIconOnClickListener {
            binding.tilLink.error = null

            var text = binding.etLink.text.toString().trim()

            if (text.isNullText())
                return@setEndIconOnClickListener

            if (!text.startsWith("http"))
                text = "http://$text"

            if (!text.isValidLink()) {
                binding.tilLink.error = getString(R.string.invalid_url)
                return@setEndIconOnClickListener
            }

            loadLinkPreview(text)
        }
    }

    private fun loadLinkPreview(link: String) {
        showProgress(true)
        val openGraphParser = OpenGraphParser(object : OpenGraphCallback {
            override fun onError(error: String) {
                showProgress(false)
            }

            override fun onPostResponse(openGraphResult: OpenGraphResult) {
                addLinkPreviewToList(openGraphResult)
            }
        }, showNullOnEmpty = true, context = this)
        openGraphParser.parse(link)
    }

    private fun addLinkPreviewToList(openGraphResult: OpenGraphResult) {
        binding.tvNoData.setVisibility(false)
        showProgress(false)
        binding.etLink.text = null

        linksList.add(openGraphResult)
        adapter.setLinksList(linksList)
        binding.rvLinkPreview.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showProgress(toShow: Boolean) {
        binding.tilLink.isEndIconVisible = !toShow
        binding.progress.setVisibility(toShow)
    }

    private fun String?.isValidLink(): Boolean {
        var isValidURL = URLUtil.isValidUrl(this)
        this?.let {
            isValidURL = isValidURL && Patterns.WEB_URL.matcher(it).matches()
        }
        return isValidURL
    }
}