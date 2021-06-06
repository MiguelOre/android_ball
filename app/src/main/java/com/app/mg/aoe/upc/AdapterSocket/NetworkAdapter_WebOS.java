//package com.app.mg.aoe.upc.AdapterSocket;


/*public class NetworkAdapter_WebOS extends ViewModel {

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    LiveData<User> user = (LiveData<User>) _user;

    public MainViewModel(
            SavedStateHandle savedStateHandle,
            UserRepository userRepository
    ) {
        String userId = savedStateHandle.get("uid");
        Call<User> userCall = userRepository.getUserById(userId);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    _user.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // show error message to user
            }
        });
    }
}

class MainViewModel extends ViewModel {

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    LiveData<User> user = (LiveData<User>) _user;

    public MainViewModel(
            SavedStateHandle savedStateHandle,
            UserRepository userRepository
    ) {
        String userId = savedStateHandle.get("uid");
        Call<User> userCall = userRepository.getUserById(userId);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    _user.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // show error message to user
            }
        });
    }
}

*/
