package nl.bakkerbart.production.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import nl.bakkerbart.production.AccountInfo
import nl.bakkerbart.production.PrivacyFragment


class TabsPagerAdapterACCOUNT(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2 // Two tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AccountInfo()
            1 -> PrivacyFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
