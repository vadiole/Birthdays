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
    private var listener: OnFragmentInteractionListener? = null

    private val birthdayViewModel by lazy {
        ViewModelProviders.of(this).get(BirthdayViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun initViews() {
            divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
            dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            dividerItemDecoration.setDrawable(divider)

            recycler_view.addItemDecoration(dividerItemDecoration)
            recycler_view.layoutManager = LinearLayoutManager(activity)
            recycler_view.adapter = adapter

            initBirthdayBottomSheet()
            initMenuBottomSheet()
            initThemeDialog()
            initSnackBar(view)
        }

        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(bottom_appbar)

        adapter = ListAdapter()
        birthdayViewModel.allBirthdaysAndHeaders.observe(this, Observer { birthdays ->
            birthdays?.let { fillAdapter(it) }
        })

        initViews()

        setFabListeners()
        setHeaderClickListener()
        setRefreshListener()
    }

    private fun fillAdapter(newList: List<MainListDataItem>) {
        adapter.itemList = newList
    }

    //listeners
    private fun setFabListeners() {
        //short click
        fab.setOnClickListener {
            if (!birthdayBottomSheetDialog.isShowing) {
                birthdayBottomSheetDialog.show()
            }
        }

        //long click
        fab.setOnLongClickListener {
            for (i in 1..12) {
                birthdayViewModel.insert(
                    Birthday(
                        "Test Birthday $i",
                        LocalDate.of(2000, i % 11 + 1, (i * 2) % 28 + 1)
                    )
                )
            }
            true
        }
    }

    private fun setHeaderClickListener() {
        header_main.setOnClickListener {
            recycler_view.smoothScrollToPosition(0)
        }
    }

    private fun setRefreshListener() {
        swipe_container.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                swipe_container.isRefreshing = false
            }, 1000)
        }
    }


    //new birthday bottom sheet
    private fun initBirthdayBottomSheet() {
        birthdayBottomSheetDialog = BottomSheetDialog(requireContext())
        birthdayBottomSheetDialog.setContentView(R.layout.fragment_birthday_bottom_sheet)

        var selectedDate: LocalDate = LocalDate.MIN
        val editText =
            birthdayBottomSheetDialog.findViewById<TextInputEditText>(R.id.input_name)
        val btnSelectDate =
            birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.select_date_btn)
        val btnSave =
            birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.save_btn)

        btnSelectDate?.setOnClickListener {
            fragmentManager?.let { it1 ->
                if (!(picker.isResumed)) {
                    picker.show(it1, picker.toString())
                }
            }
        }

        btnSave?.setOnClickListener {
            val name: String = editText?.text.toString().trim()

            if (checkIsDataValid(name, selectedDate)) {
                val birthday = Birthday(name, selectedDate, Birthday.getYearsOld(selectedDate))
                birthdayViewModel.insert(birthday)

                birthdayBottomSheetDialog.dismiss()
            }
        }

        picker.addOnPositiveButtonClickListener { selected ->
            selectedDate = LocalDate.ofEpochDay(selected / (1000 * 60 * 60 * 24))
            btnSelectDate?.text = selectedDate.toString()
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

    private fun checkIsDataValid(name: String, date: LocalDate): Boolean {
        return date != LocalDate.MIN && name.isNotBlank()
    }

    private fun setAppbarVisible(visible: Boolean) {
        if (visible) {
            bottom_appbar.visibility = View.VISIBLE
            fab.show()
        } else {
            bottom_appbar.visibility = View.INVISIBLE
            fab.hide()
        }
    }


    //right menu bottom sheet
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

    private fun showChooseThemeDialog() {
        if (!themeDialog.isShowing) {

            themeDialog.show()

            when (App.currentNightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> themeDialog.findViewById<RadioButton>(R.id.radio_btn_light)
                    ?.isChecked = true
                AppCompatDelegate.MODE_NIGHT_YES -> themeDialog.findViewById<RadioButton>(R.id.radio_btn_dark)
                    ?.isChecked = true
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> themeDialog.findViewById<RadioButton>(
                    R.id.radio_btn_follow_system
                )
                    ?.isChecked = true
            }

            themeDialog.findViewById<FrameLayout>(R.id.item_follow_system)?.visibility =
                (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) View.GONE else View.VISIBLE)

            themeDialog.findViewById<FrameLayout>(R.id.item_light)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            themeDialog.findViewById<FrameLayout>(R.id.item_dark)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
            themeDialog.findViewById<FrameLayout>(R.id.item_follow_system)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }
    }

    private fun applyNightMode(mode: Int) {
        themeDialog.dismiss()
        App.currentNightMode = mode
        App.saveNightMode(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    //theme dialog
    private fun initThemeDialog() {
        themeDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.choose_theme_dialog)
            .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    //snackbar
    private fun initSnackBar(view: View) {
        snackbar = Snackbar.make(view, "Simple snackbar", Snackbar.LENGTH_LONG)
            .setAnchorView(fab)
            .setAction("Action") {}
    }

    //option menu
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


    //lifecycle
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

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MainListFragment().apply {
//
//            }
//    }

    //views
    private lateinit var adapter: ListAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var themeDialog: AlertDialog
    private lateinit var birthdayBottomSheetDialog: BottomSheetDialog
    private lateinit var menuBottomSheetDialog: BottomSheetDialog

    private lateinit var divider: Drawable
    private lateinit var dividerItemDecoration: DividerItemDecoration

    //data picker
    private val constraintsBuilder by lazy {
        CalendarConstraints.Builder()
            .setEnd(maxData)
            .setValidator(MyDateValidator())
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
    private val maxData by lazy {
        LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
            .toEpochDay() * (1000 * 60 * 60 * 24)
    }
}
