package koslin.jan.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import koslin.jan.weather.fragments.SettingsFragment
import koslin.jan.weather.fragments.TodayFragment
import koslin.jan.weather.fragments.WeekFragment

class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2
    lateinit var viewPagerAdapter: ViewPagerAdapter
    //private val viewModel: WeatherViewModel by viewModels(factoryProducer = { WeatherViewModel.Factory })
    lateinit var settingsFragment: SettingsFragment
    lateinit var todayFragment: TodayFragment
    lateinit var weekFragment: WeekFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        viewPager.setCurrentItem(1, false)
        tabLayout.getTabAt(1)?.select()
        settingsFragment = viewPagerAdapter.fragments[0] as SettingsFragment
        todayFragment = viewPagerAdapter.fragments[1] as TodayFragment
        weekFragment = viewPagerAdapter.fragments[2] as WeekFragment

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })

    }
}