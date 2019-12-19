package vadiole.birthdays

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_main_list.*
import org.threeten.bp.LocalDate


class MainListFragment : Fragment() {
    private val maxData = LocalDate.of(
        LocalDate.now().year,
        LocalDate.now().month,
        1
    ).toEpochDay() * (1000 * 60 * 60 * 24)

    private var listener: OnFragmentInteractionListener? = null

    private val birthdayViewModel by lazy {
        ViewModelProviders.of(this).get(BirthdayViewModel::class.java)
    }

    private lateinit var snackbar: Snackbar
    private lateinit var dialog: AlertDialog
    private lateinit var birthdayBottomSheetDialog: BottomSheetDialog

    private lateinit var menuBottomSheetDialog: BottomSheetDialog
    private lateinit var divider: Drawable
    private lateinit var dividerItemDecoration: DividerItemDecoration

    //data picker
    private val constraintsBuilder by lazy {
        CalendarConstraints.Builder()
            .setEnd(maxData)
            .setValidator(MyDateValidator())
//            .setOpenAt(maxData)
    }
    private val builder by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.selectDataOfBirthday)
            .setTheme(R.style.AppTheme_MaterialDataPicker)
            .setCalendarConstraints(constraintsBuilder.build())

    }
    private val picker by lazy {
        builder.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(bottom_appbar)

        val adapter = ListAdapter()
        birthdayViewModel.allBirthdaysAndHeaders.observe(this, Observer { birthdays ->
            birthdays?.let { adapter.refreshBirthdays(it) }
        })


        divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
        dividerItemDecoration.setDrawable(divider)

        main_birthday_list.addItemDecoration(dividerItemDecoration)
        main_birthday_list.layoutManager = LinearLayoutManager(activity)
        main_birthday_list.adapter = adapter




        setFabListeners()
        setHeaderClickListener()
setRefreshListener()

        initBirthdayBottomSheet()
        initMenuBottomSheet()
        initDialog()
        initSnackBar(view)
    }

    private fun setRefreshListener() {
        swipe_container.setOnRefreshListener {
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                swipe_container.isRefreshing = false
            }

            handler.postDelayed(runnable, 1000)
        }
    }

    private fun setFabListeners() {
        fab.setOnClickListener {
            if (!birthdayBottomSheetDialog.isShowing) {
                birthdayBottomSheetDialog.show()
            }
        }

//        Long Click
        fab.setOnLongClickListener {
            for (i in 1..12) {
                birthdayViewModel.insert(Birthday("Test Birthday $i", LocalDate.of(2000, i, i * 2)))
            }
            true
        }
    }

    private fun setHeaderClickListener() {
        header_main.setOnClickListener {
            main_birthday_list.smoothScrollToPosition(0)
        }
    }

    private fun initBirthdayBottomSheet() {
        birthdayBottomSheetDialog = BottomSheetDialog(requireContext())
        birthdayBottomSheetDialog.setContentView(R.layout.fragment_birthday_bottom_sheet)
        var selectedDate: LocalDate = LocalDate.MIN

        val editText = birthdayBottomSheetDialog.findViewById<TextInputEditText>(R.id.input_name)
        val btnSelectDate =
            birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.select_date_btn)

        btnSelectDate?.setOnClickListener {
            fragmentManager?.let { it1 ->
                if (!(picker.isResumed)) {
                    picker.show(it1, picker.toString())
                }
            }
        }

        picker.addOnPositiveButtonClickListener { selected ->
            selectedDate = LocalDate.ofEpochDay(selected / (1000 * 60 * 60 * 24))
            btnSelectDate?.text = selectedDate.toString()
        }

        birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.save_btn)
            ?.setOnClickListener {
                val name: String = editText?.text.toString().trim()

                if (checkIsDataValid(name, selectedDate)) {
                    val birthday = Birthday(name, selectedDate, Birthday.getYearsOld(selectedDate))
                    birthdayViewModel.insert(birthday)
                    birthdayBottomSheetDialog.dismiss()
                }
            }

        birthdayBottomSheetDialog.setOnShowListener {
            setAppbarVisible(false)
        }

        birthdayBottomSheetDialog.setOnDismissListener {
            if (picker.isVisible) {
                picker.dismiss()
            }
            setAppbarVisible(true)
            selectedDate = LocalDate.MIN
            btnSelectDate?.setText(R.string.select_date_of_birthday)
            editText?.setText("")
        }
    }

    private fun initMenuBottomSheet() {
        menuBottomSheetDialog = BottomSheetDialog(requireContext())
        menuBottomSheetDialog.setContentView(R.layout.fragment_menu_bottom_sheet)

        menuBottomSheetDialog.findViewById<LinearLayout>(R.id.menu_theme)
            ?.setOnClickListener {
                menuBottomSheetDialog.dismiss()
                showChooseThemeDialog()
            }

        menuBottomSheetDialog.findViewById<LinearLayout>(R.id.menu_advanced_settings)
            ?.setOnLongClickListener {
                menuBottomSheetDialog.dismiss()
                birthdayViewModel.deleteAll()
                Toast.makeText(requireContext(), "Clear all", Toast.LENGTH_SHORT).show()
                true
            }
    }


    private fun initSnackBar(view: View) {
        snackbar = Snackbar.make(view, "Simple snackbar", Snackbar.LENGTH_LONG)
            .setAnchorView(fab)
            .setAction("Action") {}
    }

    private fun setAppbarVisible(b: Boolean) {
        if (b) {
            bottom_appbar.visibility = View.VISIBLE
            fab.show()
        } else {
            bottom_appbar.visibility = View.INVISIBLE

            fab.hide()
        }
    }

    private fun checkIsDataValid(name: String, date: LocalDate): Boolean {
        return date != LocalDate.MIN && name.isNotBlank()
    }

    private fun initDialog() {
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.choose_theme_dialog)
            .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun addSnackBarListeners(materialCalendarPicker: MaterialDatePicker<*>) {
        materialCalendarPicker.addOnPositiveButtonClickListener {
            snackbar.setText(materialCalendarPicker.headerText)
            snackbar.show()
        }
        materialCalendarPicker.addOnNegativeButtonClickListener {
            snackbar.setText("нажата отмена")
            snackbar.show()
        }
        materialCalendarPicker.addOnCancelListener {
            snackbar.setText("отмена")
            snackbar.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                if (!menuBottomSheetDialog.isShowing) {
                    menuBottomSheetDialog.show()

                    when (App.currentNightMode) {
                        AppCompatDelegate.MODE_NIGHT_NO -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.light)
                        AppCompatDelegate.MODE_NIGHT_YES -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.dark)
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.follow_system)
                    }

                    menuBottomSheetDialog.findViewById<TextView>(R.id.selected_theme)?.text
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showChooseThemeDialog() {
        dialog.show()

        when (App.currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> dialog.findViewById<RadioButton>(R.id.radio_btn_light)
                ?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> dialog.findViewById<RadioButton>(R.id.radio_btn_dark)
                ?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> dialog.findViewById<RadioButton>(R.id.radio_btn_follow_system)
                ?.isChecked = true
        }

        dialog.findViewById<FrameLayout>(R.id.item_follow_system)?.visibility =
            (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) View.GONE else View.VISIBLE)

        dialog.findViewById<FrameLayout>(R.id.item_light)
            ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
        dialog.findViewById<FrameLayout>(R.id.item_dark)
            ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
        dialog.findViewById<FrameLayout>(R.id.item_follow_system)
            ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
    }


    private fun applyNightMode(mode: Int) {
        dialog.dismiss()
        App.currentNightMode = mode
        App.saveNightMode(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainListFragment().apply {

            }
    }
}
