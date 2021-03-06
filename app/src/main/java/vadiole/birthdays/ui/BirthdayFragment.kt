package vadiole.birthdays.ui

import android.animation.AnimatorInflater
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_birthday.*
import kotlinx.android.synthetic.main.fragment_birthday_content.*
import org.threeten.bp.LocalDate
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.R
import vadiole.birthdays.models.Birthday
import vadiole.birthdays.utils.Event
import vadiole.birthdays.utils.LogUtil


class BirthdayFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    var birthday = Birthday(" ", LocalDate.MIN)

    private val birthdayViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
    }

    private var isFullyCreated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (myView == null) {
            Log.d(LogUtil.BIRTHDAY_FRAGMENT, ": view reinflated")
            myView = inflater.inflate(R.layout.fragment_birthday, container, false)
            isFullyCreated = false
        } else {
            Log.d(LogUtil.BIRTHDAY_FRAGMENT, ": view restored")
        }
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun phoneItemClick() {
            val number = birthday.phoneNumber
            val makeCall = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(makeCall)
        }

        fun telegramItemClick() {
            val telegramId = birthday.telegramLink
            val telegram = Intent(
                Intent.ACTION_VIEW, Uri.parse("https://telegram.me/$telegramId")
            )
            startActivity(telegram)
        }

        fun instagramItemClick() {
            val instagramId = birthday.instagramLink
            val instagram = Intent(
                Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$instagramId")
            )
            startActivity(instagram)
        }

        fun emailItemClick() {
            val emailAddress = birthday.email
            val email = Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:$emailAddress"))
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress.orEmpty()))
            startActivity(Intent.createChooser(email, "Send email with:"))
        }

        fun notesItemClick() {
            val notes = birthday.notes
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Notes", "$notes")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.notes_copied), Toast.LENGTH_SHORT)
                .show()
        }

        fun giftsItemClick() {
            val gifts = birthday.gifts
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Notes", "$gifts")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.gifts_copied), Toast.LENGTH_SHORT)
                .show()
        }

        fun initViews(b: Birthday) {
            //person data
            name_birthday.text = b.name
            date_birthday.text = b.birthdayDate.toString()

            if (b.phoneNumber.isNullOrEmpty()) phone_item.visibility = View.GONE
            else {
                phone_item.visibility = View.VISIBLE
                phone_item.setOnClickListener { phoneItemClick() }
                phone_number.text = b.phoneNumber
            }

            if (b.telegramLink.isNullOrEmpty()) telegram_item.visibility = View.GONE
            else {
                telegram_item.visibility = View.VISIBLE
                telegram_item.setOnClickListener { telegramItemClick() }
                telegram.text = b.telegramLink
            }

            if (b.instagramLink.isNullOrEmpty()) instagram_item.visibility = View.GONE
            else {
                instagram_item.visibility = View.VISIBLE
                instagram_item.setOnClickListener { instagramItemClick() }
                instagram.text = b.instagramLink
            }

            if (b.email.isNullOrEmpty()) email_item.visibility = View.GONE
            else {
                email_item.visibility = View.VISIBLE
                email_item.setOnClickListener { emailItemClick() }
                email.text = b.email
            }

            if (b.notes.isNullOrEmpty()) notes_item.visibility = View.GONE
            else {
                notes_item.visibility = View.VISIBLE
                notes_item.setOnClickListener { notesItemClick() }
                note.text = b.notes
            }

            if (b.gifts.isNullOrEmpty()) gifts_item.visibility = View.GONE
            else {
                gifts_item.visibility = View.VISIBLE
                gifts_item.setOnClickListener { giftsItemClick() }
                gifts.text = b.gifts
            }

            //dividers
            divider_contacts.visibility = View.GONE
            if (phone_item.isVisible || telegram_item.isVisible || instagram_item.isVisible || email_item.isVisible)
                divider_contacts.visibility = View.VISIBLE


            if (phone_item.isVisible and telegram_item.isVisible) {
                divider_telegram.visibility = View.VISIBLE
            } else divider_telegram.visibility = View.GONE

            if (telegram_item.isVisible and instagram_item.isVisible) {
                divider_instagram.visibility = View.VISIBLE
            } else divider_instagram.visibility = View.GONE

            if (instagram_item.isVisible and email_item.isVisible) {
                divider_email.visibility = View.VISIBLE
            } else divider_email.visibility = View.GONE

            divider_notes.visibility = View.GONE
            if (notes_item.isVisible || gifts_item.isVisible)
                divider_notes.visibility = View.VISIBLE

            if (notes_item.isVisible and gifts_item.isVisible) {
                divider_gifts.visibility = View.VISIBLE
            } else divider_gifts.visibility = View.GONE
        }


        // fix android navigation bug
        // https://github.com/ncapdevi/FragNav/issues/103
        if (!birthdayViewModel.transactionLocked) {
            birthdayViewModel.transactionLocked = true
            enableDisableView(view, false)

            Handler().postDelayed({
                birthdayViewModel.transactionLocked = false
                enableDisableView(view, true)
            }, resources.getInteger(R.integer.nav_anim_duration).toLong())
        }

        setListeners()

        birthdayViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            if (Birthday.isNull(it)) {
                onButtonPressed(Event.BackPressed)
            } else {
                birthday = it
                initViews(it)
            }
        })
        playFabEnterAnim()
    }

    private fun playFabEnterAnim() {
        fab_edit.elevation = 0f
        fab_edit.translationZ = 0f
        fab_edit.stateListAnimator = null
        fab_edit.animate()
            .translationZ(resources.getDimension(R.dimen.fab_normal_elevation))
            .setInterpolator(object : LinearOutSlowInInterpolator() {})
            .setStartDelay((resources.getInteger(R.integer.nav_anim_duration) * 0.75).toLong())
            .setDuration(resources.getInteger(R.integer.button_fade_in_anim_duration).toLong())
            .withLayer()
            .withEndAction {
                if (fab_edit != null) {
                    fab_edit.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                        requireContext(),
                        R.animator.extended_fab_state_list_animator
                    )
                }
            }
            .start()
    }

    private fun setListeners() {
        delete_btn.setOnClickListener {
            if (!Birthday.isNull(birthday)) {
                birthdayViewModel.delete(birthday)
                onButtonPressed(Event.DelBirthdayPressed)
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        back_btn.setOnClickListener {
            onButtonPressed(Event.BackPressed)
        }

        fab_edit.setOnClickListener {
            onButtonPressed(Event.OpenEditBirthdayFragment)
        }
    }

    private fun enableDisableView(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (idx in 0 until view.childCount) {
                enableDisableView(view.getChildAt(idx), enabled)
            }
        }
    }

    private fun onButtonPressed(event: Event) {
        listener?.onFragmentInteraction(event)
    }

    override fun onAttach(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }

    companion object {
//        @JvmStatic
//        fun newInstance() =
//            BirthdayFragment().apply {}

        var myView: View? = null

    }
}
