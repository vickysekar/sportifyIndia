package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Notes;
import com.sportifyindia.app.domain.Task;
import com.sportifyindia.app.service.dto.NotesDTO;
import com.sportifyindia.app.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notes} and its DTO {@link NotesDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotesMapper extends EntityMapper<NotesDTO, Notes> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    NotesDTO toDto(Notes s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
