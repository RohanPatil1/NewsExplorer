package com.rohan.newsexplorer.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rohan.newsexplorer.databinding.ActivityMainBinding
import com.rohan.newsexplorer.ui.view_models.MainViewModel
import com.rohan.newsexplorer.ui.worker.NewsWorker
import com.rohan.newsexplorer.utils.Constants.IS_FRESH_START
import com.rohan.newsexplorer.utils.Constants.CAN_SHOW_ONLINE
import com.rohan.newsexplorer.utils.Constants.SHARED_PREFS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel by viewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        handleNetworkUI()

        //Schedule BG Worker
        scheduleBgWorker()
    }

    private fun scheduleBgWorker() {
        val request = OneTimeWorkRequestBuilder<NewsWorker>().build()

        //Add request to queue
        WorkManager.getInstance(applicationContext)
            .enqueue(request)
        val requestId = request.id
        //Observe
        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(requestId)
            .observe(this) {
                val TAG = "Worker"
                it?.let { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED ->
                            Log.d(TAG, "Worker ENQUEUED")
                        WorkInfo.State.RUNNING ->
                            Log.d(TAG, "Worker RUNNING")
                        WorkInfo.State.SUCCEEDED ->
                            Log.d(TAG, "Worker SUCCEEDED")
                        WorkInfo.State.FAILED ->
                            Log.d(TAG, "Worker FAILED")
                        WorkInfo.State.BLOCKED ->
                            Log.d(TAG, "Worker BLOCKED")
                        WorkInfo.State.CANCELLED ->
                            Log.d(TAG, "Worker CANCELLED")
                    }
                }
            }
    }

    private fun handleNetworkUI() {
        Log.d("ConnManager", "MainActivity: INIT")
        binding.mainNetworkOnTV.visibility = INVISIBLE
        binding.mainNetworkOffTV.visibility = INVISIBLE

        mainViewModel.networkLiveData.observe(this) {
            Log.d("ConnManager", "MainActivity: isNetworkAvailable- $it")


            if (it) {
                Log.d("ConnManager", "MainActivity: IN TRUE")

                //Network ON

                //Show backOnline when we go from off->on & not a first time opening
                val b1 = prefs.getBoolean(CAN_SHOW_ONLINE, false)

                Log.d("ConnManager", "MainActivity: OUT b1 :$b1")

                if (prefs.getBoolean(CAN_SHOW_ONLINE, false)) {
                    Log.d("ConnManager", "MainActivity: showing back ol")
                    binding.mainNetworkOnTV.visibility = VISIBLE
                    binding.mainNetworkOffTV.visibility = GONE
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.mainNetworkOnTV.visibility = GONE
                        }

                    }
                    updateShowOnlineFlag(false)
                }
            } else {
                Log.d("ConnManager", "MainActivity: IN FALSE")

                //Network OFF
                updateShowOnlineFlag(true)
                binding.mainNetworkOnTV.visibility = GONE
                binding.mainNetworkOffTV.visibility = VISIBLE
            }

        }
    }

    private fun updateShowOnlineFlag(value: Boolean) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(CAN_SHOW_ONLINE, value)
        editor.apply()
    }

}

fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.let {
        Toast.makeText(it, text, duration).show()
    }
}
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
