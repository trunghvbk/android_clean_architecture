package com.example.androidcleanarchitecture.data.mapper

import com.example.androidcleanarchitecture.data.model.UserModel
import com.example.androidcleanarchitecture.data.model.UserNetworkModel

/**
 * Mapper class to convert between UserNetworkModel and UserModel.
 * This handles the transformation between network layer models and data layer models.
 */
class UserNetworkMapper {
    
    /**
     * Maps a network model to a data model
     * @param networkModel The network model to convert
     * @return The converted data model
     */
    fun mapToData(networkModel: UserNetworkModel): UserModel {
        return UserModel(
            id = networkModel.id,
            name = networkModel.name,
            email = networkModel.email,
        )
    }
    
    /**
     * Maps a data model to a network model
     * @param dataModel The data model to convert
     * @return The converted network model
     */
    fun mapToNetwork(dataModel: UserModel): UserNetworkModel {
        return UserNetworkModel(
            id = dataModel.id,
            name = dataModel.name,
            email = dataModel.email,
            username = ""
        )
    }
    
    /**
     * Maps a list of network models to a list of data models
     * @param networkModels The list of network models to convert
     * @return The list of converted data models
     */
    fun mapToDataList(networkModels: List<UserNetworkModel>): List<UserModel> {
        return networkModels.map { mapToData(it) }
    }
}
