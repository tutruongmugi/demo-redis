package com.example.demo_redis.services;

import com.example.demo_redis.models.Book;
import com.example.demo_redis.models.Cart;
import com.example.demo_redis.models.CartItem;
import com.example.demo_redis.models.User;
import com.example.demo_redis.repositories.BookRepository;
import com.example.demo_redis.repositories.CartRepository;
import com.example.demo_redis.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.Path;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

    Path cartItemsPath = Path.of(".cartItems");

    public Cart get(String id) {
        return cartRepository.findById(id).orElse(null);
    }

    public void addToCart(String id, CartItem item) {
        Optional<Book> book = bookRepository.findById(item.getIsbn());
        if (book.isPresent()) {
            item.setPrice(book.get().getPrice());
            jedis.jsonArrAppend(CartRepository.getKey(id), cartItemsPath, item);
        }
    }

    public void removeFromCart(String id, String isbn) {
        Optional<Cart> cartFinder = cartRepository.findById(id);
        if (cartFinder.isPresent()) {
            Cart cart = cartFinder.get();
            String cartKey = CartRepository.getKey(id);
            List<CartItem> cartItems = new ArrayList<CartItem>(cart.getCartItems());
            OptionalInt cartItemIndex =  IntStream.range(0, cartItems.size())
                    .filter(i -> cartItems.get((int) i).getIsbn().equals(isbn))
                    .findFirst();
            if (cartItemIndex.isPresent()) {
                jedis.jsonArrPop(cartKey, CartItem.class, cartItemsPath, cartItemIndex.getAsInt());
            }
        }
    }

    public void checkout(String id) {
        Cart cart = cartRepository.findById(id).get();
        User user = userRepository.findById(cart.getUserId()).get();
        cart.getCartItems().forEach(item -> {
            Book book = bookRepository.findById(item.getIsbn()).get();
            user.addBook(book);
        });
        userRepository.save(user);
    }
}
