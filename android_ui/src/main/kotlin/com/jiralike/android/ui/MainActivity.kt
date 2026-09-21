package com.jiralike.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.jiralike.android.R
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.databinding.ActivityMainDrawerBinding
import com.jiralike.android.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainDrawerBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem)
            true
        }

        // Load default fragment
        if (savedInstanceState == null) {
            binding.navigationView.setCheckedItem(R.id.nav_dashboard)
            handleNavigation(binding.navigationView.menu.findItem(R.id.nav_dashboard))
        }
    }

    private fun handleNavigation(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_logout) {
            SessionManager.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val fragment: Fragment = when (menuItem.itemId) {
            R.id.nav_dashboard -> DashboardFragment()
            R.id.nav_groups -> GroupsFragment()
            R.id.nav_settings -> SettingsFragment()
            R.id.nav_admin -> AdminFragment()
            else -> DashboardFragment()
        }

        binding.toolbar.title = menuItem.title
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}
