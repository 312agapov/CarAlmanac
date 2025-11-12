package ru.agapovla.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.agapovla.entity.Photo;
import ru.agapovla.repository.PhotoRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public List<Photo> addPhotos(UUID carOrPartId, List<String> pathsToPhotos){
        List<Photo> addPhotos = pathsToPhotos.stream()
                .map(path -> {
                    Photo photo = new Photo();
                    photo.setCarOrPartId(carOrPartId);
                    photo.setFilePath(path);
                    return photo;
                })
                .toList();
        return photoRepository.saveAll(addPhotos);
    }

    public List<Photo> getPhotosByCarOrPartUUID(UUID carOrPartId){
        return photoRepository.findByCarOrPartId(carOrPartId);
    }

    public void deletePhotoById(UUID photoId) {
        photoRepository.deleteById(photoId);
    }
}
