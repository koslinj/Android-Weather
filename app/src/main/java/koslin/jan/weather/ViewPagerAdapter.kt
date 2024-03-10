package koslin.jan.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import koslin.jan.weather.fragments.SettingsFragment
import koslin.jan.weather.fragments.TodayFragment
import koslin.jan.weather.fragments.WeekFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return SettingsFragment()
            1 -> return TodayFragment()
            2 -> return WeekFragment()
            else -> return TodayFragment()
        }
    }
}