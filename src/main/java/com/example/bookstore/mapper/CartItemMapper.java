package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.CartItemResponseDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(target = "shoppingCart", ignore = true)
    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookById")
    CartItem toModel(CartItemRequestDto requestDto);

    void updateCartItemFromDto(@MappingTarget CartItem cartItem,
                               UpdateCartItemDto requestItem);

    @Named("bookById")
    default Book bookById(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
