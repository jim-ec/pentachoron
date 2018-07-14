package io.jim.tesserapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.SymbolicColorMapping
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.ui.preferences.PreferencesActivity
import io.jim.tesserapp.util.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {
    
    lateinit var viewModel: MainViewModel
    
    companion object {
        
        /**
         * The preference activity is started with this identifying request code,
         * so that the callback [onActivityResult] can check whether is was called
         * because the user exited the preference activity.
         *
         * This will cause the activity to be recreated, as changing preferences affects
         * immutable state as the theme (which can be dark or light) or geometry like
         * the grid.
         */
        const val PREFERENCES_REQUEST = 1
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return CONSUMED
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return NOT_CONSUMED
        
        when (item.itemId) {
            R.id.appbar_menu_options -> {
                startActivityForResult(
                        Intent(this, PreferencesActivity::class.java),
                        PREFERENCES_REQUEST)
            }
        }
        
        return CONSUMED
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PREFERENCES_REQUEST) {
            // Since preference changes affects immutable activity state like theme,
            // I just recreate the activity every times the user returned from
            // the preference screen:
            recreate()
        }
    }
    
    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fetch view model:
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        
        viewModel.synchronized {
            symbolicColorMapping = SymbolicColorMapping(
                    primary = Color(themedColorInt(R.attr.colorPrimaryGeometry)),
                    accent = Color(themedColorInt(R.attr.colorAccent)),
                    x = Color(themedColorInt(R.attr.colorAxisX)),
                    y = Color(themedColorInt(R.attr.colorAxisY)),
                    z = Color(themedColorInt(R.attr.colorAxisZ)),
                    q = Color(themedColorInt(R.attr.colorAccent))
            )
        }
        
        // Set theme, which is only possible during this callback:
        setTheme(preferenceThemeId())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar.apply { setTitle(R.string.tesseract) })
        
        // Generate geometry:
        viewModel.createGeometries(
                featuredGeometryName = getString(R.string.tesseract),
                enableGrid = gridPreference()
        )
        
        // Associate the controller with the graphics view to control:
        controllerView.targetGraphicsView = graphicsView
    }
    
    /**
     * Pause render-thread.
     */
    override fun onStop() {
        super.onStop()
        graphicsView.onPause()
    }
    
    /**
     * Resume render-thread.
     */
    override fun onResume() {
        super.onResume()
        graphicsView.onResume()
    }
    
}
