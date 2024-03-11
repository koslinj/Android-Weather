package koslin.jan.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import koslin.jan.weather.fragments.SettingsFragment
import koslin.jan.weather.fragments.TodayFragment
import koslin.jan.weather.fragments.WeekFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    // Store references to your fragments
    var fragments: List<Fragment> = listOf(SettingsFragment(), TodayFragment(), WeekFragment())
        private set

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}