package eu.groeller.datastreamui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import eu.groeller.datastreamui.data.HttpClientConfigurer
import eu.groeller.datastreamui.data.user.UserRepository
import eu.groeller.datastreamui.data.datasource.LocalDataSource
import eu.groeller.datastreamui.data.datasource.UserNetworkDataSource
import eu.groeller.datastreamui.viewmodel.DashViewModel

class InjectionManager(private val datastore: DataStore<Preferences>) {
    val httpConfig = HttpClientConfigurer()
    val localDataSource: LocalDataSource = LocalDataSource(datastore)
    val userNetworkDataSource = UserNetworkDataSource(httpConfig.v1HttpClient)
    val userRepository = UserRepository(localDataSource, userNetworkDataSource)
}