/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.devbyteviewer.R
import com.example.android.devbyteviewer.databinding.DevbyteItemBinding
import com.example.android.devbyteviewer.databinding.FragmentDevByteBinding
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.viewmodels.DevByteViewModel

/**
 * Show a list of DevBytes on screen.
 */
class DevByteFragment : Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: DevByteViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, DevByteViewModel.Factory(activity.application))
                .get(DevByteViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of Video to cards.
     */
    private var viewModelAdapter: DevByteAdapter? = null

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.playlist.observe(viewLifecycleOwner, Observer<List<DevByteVideo>> { videos ->
            videos?.apply {
                viewModelAdapter?.videos = videos
            }
        })
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentDevByteBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_dev_byte,
                container,
                false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = DevByteAdapter(VideoClick {
            // When a video is clicked this block or lambda will be called by DevByteAdapter

            // context is not around, we can safely discard this click since the Fragment is no
            // longer on the screen
            val packageManager = context?.packageManager ?: return@VideoClick

            // Try to generate a direct intent to the YouTube app
            var intent = Intent(Intent.ACTION_VIEW, it.launchUri)
            if(intent.resolveActivity(packageManager) == null) {
                // YouTube app isn't found, use the web url
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
            }

            startActivity(intent)
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }


        // Observer for the network error.
        viewModel.eventNetworkError.observe(this, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    /**
     * Helper method to generate YouTube app links
     */
    private val DevByteVideo.launchUri: Uri
        get() {
            val httpUri = Uri.parse(url)
            return Uri.parse("vnd.youtube:" + httpUri.getQueryParameter("v"))
        }
}

/**
 * Click listener for Videos. By giving the block a name it helps a reader understand what it does.
 *
 */
class VideoClick(val block: (DevByteVideo) -> Unit) {
    /**
     * Called when a video is clicked
     *
     * @param video the video that was clicked
     */
    fun onClick(video: DevByteVideo) = block(video)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class DevByteAdapter(val callback: VideoClick) : RecyclerView.Adapter<DevByteViewHolder>() {

    /**
     * The videos that our Adapter will show
     */
    var videos: List<DevByteVideo> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevByteViewHolder {
        val withDataBinding: DevbyteItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                DevByteViewHolder.LAYOUT,
                parent,
                false)
        return DevByteViewHolder(withDataBinding)
    }

    override fun getItemCount() = videos.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: DevByteViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.video = videos[position]
            it.videoCallback = callback
        }
    }

}

/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class DevByteViewHolder(val viewDataBinding: DevbyteItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.devbyte_item
    }
}