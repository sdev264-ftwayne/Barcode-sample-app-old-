package edu.ivytech.rootbeer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import edu.ivytech.rootbeer.database.RootBeer
import java.util.*

class RootBeerDetailViewModel : ViewModel() {
    private val repo = RootBeerRepository.get()
    private val rootBeerIdLiveData = MutableLiveData<UUID>()
    var rootBeerLiveData : LiveData<RootBeer> = rootBeerIdLiveData.switchMap() {
            id -> repo.getRootBeer(id)
    }
    fun loadRootBeer(id : UUID) {
        rootBeerIdLiveData.value = id
    }
    fun addRootBeer(rootBeer:RootBeer) {
        repo.addRootBeer(rootBeer)
    }
    fun saveRootBeer(rootBeer: RootBeer) {
        repo.updateRootBeer(rootBeer)
    }
}