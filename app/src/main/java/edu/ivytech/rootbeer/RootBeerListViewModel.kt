package edu.ivytech.rootbeer

import androidx.lifecycle.ViewModel

class RootBeerListViewModel: ViewModel() {
    private val repo = RootBeerRepository.get()
    val rootBeerListLiveData = repo.getAllRootBeer()
}