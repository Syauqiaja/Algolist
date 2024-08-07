package com.aglotest.algolist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aglotest.algolist.data.database.AlgolistDatabase
import com.aglotest.algolist.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var database: AlgolistDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            viewModel.isLoading.observe(this@MainActivity){ isLoading ->
                setKeepOnScreenCondition(){
                    return@setKeepOnScreenCondition !isLoading
                }
            }
        }
        initDatabase()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun initDatabase() {
        lifecycleScope.launch {
            database.getTaskDao().getAllTasks().first()
        }
    }

    private fun setupNavigation() {
        setSupportActionBar(null)
    }
}