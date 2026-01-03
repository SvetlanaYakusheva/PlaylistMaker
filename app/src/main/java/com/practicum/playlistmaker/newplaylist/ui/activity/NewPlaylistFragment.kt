package com.practicum.playlistmaker.newplaylist.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.util.dpToPx
import java.io.File
import java.io.FileOutputStream


import com.practicum.playlistmaker.newplaylist.ui.presentation.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment: Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NewPlaylistViewModel>()

    private var textWatcher: TextWatcher? = null
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    var imageCoverUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Отмена") { dialog, which ->
                //  закрывается диалог, пользователь остаётся на экране создания плейлиста

            }
            .setNegativeButton("Завершить") {
                    dialog, which ->
                // экран создания плейлиста закрывается и пользователь возвращается на предыдущий экран
                findNavController().navigateUp()
            }

        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.inputEditTextPlaylistName.text.isNullOrEmpty() or !binding.inputEditTextPlaylistDescription.text.isNullOrEmpty() or (imageCoverUri != null)) {
                    confirmDialog.show()
                } else {
                    findNavController().navigateUp()
                }
            }
        })


        binding.backButton.setOnClickListener {
            if (!binding.inputEditTextPlaylistName.text.isNullOrEmpty() or !binding.inputEditTextPlaylistDescription.text.isNullOrEmpty() or (imageCoverUri != null)) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createPlaylistButton.isEnabled = !s.isNullOrEmpty()

            }
            override fun afterTextChanged(s: Editable?) { }
        }
        binding.inputEditTextPlaylistName.addTextChangedListener(textWatcher)

        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {


                    Glide.with(this)
                        .load(uri)
                        .transform(CenterCrop(), RoundedCorners(dpToPx(8f, requireContext())))
                        .placeholder(R.drawable.ic_add_picture_80)
                        .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    imageCoverUri = null
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    // Действие при успешной загрузке
                                    imageCoverUri = uri
                                    return false
                                }
                        })
                        .into(binding.newPlaylistCover)
                } else {
                    imageCoverUri = null
                }
            }
        //по нажатию на кнопку pickImage запускаем photo picker
        binding.newPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            imageCoverUri?.let { saveImageToPrivateStorage(it) }
            viewModel.onCreateNewPlaylistButtonClicked(
                binding.inputEditTextPlaylistName.text.toString(),
                binding.inputEditTextPlaylistDescription.text.toString(),
                imageCoverUri
            )
            findNavController().navigateUp()
            Toast.makeText(requireContext(), "Плейлист ${binding.inputEditTextPlaylistName.text} создан", Toast.LENGTH_LONG).show()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}
