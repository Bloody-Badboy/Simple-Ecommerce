package dev.arpan.ecommerce.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.arpan.ecommerce.data.ProductsRepository

class MainViewModel @ViewModelInject constructor(private val repository: ProductsRepository) :
    ViewModel() {

}