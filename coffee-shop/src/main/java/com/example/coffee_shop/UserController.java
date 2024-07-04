package com.example.coffee_shop;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/login")
    public String index(HttpSession session, Model model) {

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartQty", cartItems.size());

        double totalAmount = calculateTotal(cartItems);
        DecimalFormat df = new DecimalFormat("#0.00");
        String formattedTotal = df.format(totalAmount);

        model.addAttribute("total",formattedTotal);


        return "login";



    }
    @PostMapping("/login-verify")
    public String login(@RequestParam String email, @RequestParam String
            password, HttpSession session, Model model) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {


        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            return "redirect:/";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }


    @GetMapping("/signup")
    public String addUserForm(HttpSession session,Model model) {


        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartQty", cartItems.size());

        double totalAmount = calculateTotal(cartItems);
        DecimalFormat df = new DecimalFormat("#0.00");
        String formattedTotal = df.format(totalAmount);

        model.addAttribute("total",formattedTotal);


        model.addAttribute("user", new User());
        return "signup";
    }
    @PostMapping("/signup-verify")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {

        String password = user.getPassword();
        String verifypassword = user.getVerifypassword();


        if(password.equals(verifypassword)){

            redirectAttributes.addFlashAttribute("success", "Account Created Successfully");


            userService.saveUser(user);
            return "redirect:/login";

        }else{


            redirectAttributes.addFlashAttribute("error", "Password and Confirm Password Need to be same");

            return "redirect:/signup";



        }



    }

    private double calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalAmount();
        }
        return total;
    }

}
