package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.User;
import com.giantlink.project.models.requests.UserRequest;
import com.giantlink.project.models.responses.UserResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	UserResponse mapEntity(User entity);

	User mapRequest(UserRequest entity);

	User mapResponse(UserResponse entity);

	Set<UserResponse> mapResponses(Set<User> entities);

	List<UserResponse> mapResponses(List<User> findAll);
}
