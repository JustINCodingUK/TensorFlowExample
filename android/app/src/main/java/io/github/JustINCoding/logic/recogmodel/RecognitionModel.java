package io.github.JustINCoding.logic.recogmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RecognitionModel extends ViewModel {
      private MutableLiveData tempRecogList = new MutableLiveData<List<Recognition>>();
      public LiveData<List<Recognition>> recogList = tempRecogList;

      public void update(List<Recognition> recogs){
          tempRecogList.postValue(recogs);
      }
}


