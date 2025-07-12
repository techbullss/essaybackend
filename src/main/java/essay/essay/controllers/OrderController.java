package essay.essay.controllers;
import essay.essay.Models.Order;
import essay.essay.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderRepo orderRepository;



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(
            @RequestParam("topic") String topic,
            @RequestParam("instructions") String instructions,
            @RequestParam("level") String level,
            @RequestParam("assignmentType") String assignmentType,
            @RequestParam("style") String style,
            @RequestParam("deadline") String deadline,
            @RequestParam("slides") int slides,
            @RequestParam("spacing") String spacing,
            @RequestParam("pages") int pages,
            @RequestParam("amount") double amount,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        try {
            List<String> fileUrls = new ArrayList<>();
            List<String> filenames=new ArrayList<>();
            if (files != null && files.length > 0) {
                String uploadDir = "uploads"; // directory relative to your working directory
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : files) {
                    String originalFilename = file.getOriginalFilename();
                    String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
                    Path filePath = uploadPath.resolve(uniqueFilename);

                    // Save file to disk
                    Files.write(filePath, file.getBytes());

                    // Save local path or public URL
                    String fileUrl = "/uploads/" + uniqueFilename; // You can adjust this for actual frontend access
                    fileUrls.add(fileUrl);
                    filenames.add(originalFilename);
                }
            }

            Order order = new Order();
            order.setTopic(topic);
            order.setInstructions(instructions);
            order.setLevel(level);
            order.setAssignmentType(assignmentType);
            order.setStyle(style);
            order.setDeadline(String.valueOf(LocalDate.parse(deadline)));
            order.setSlides(slides);
            order.setSpacing(spacing);
            order.setPages(pages);
            order.setAmount(amount);
            order.setOrderStatus("pending");

            order.setFileUrls(fileUrls);
            order.setFilenames(filenames);
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {
            return  ResponseEntity.badRequest()
                    .body("Processing failed: " + e.getMessage());
        }
    }
    @GetMapping("api/orders")
    public ResponseEntity<Page<Order>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {  // "asc" or "desc"

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        Page<Order> orders = orderRepository.findAll(pageable);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("api/orderslisting")
    public ResponseEntity<Page<Order>>sortorder(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size,
                    @RequestParam(defaultValue = "id") String sortBy,
                    @RequestParam(defaultValue = "desc") String direction,
                    @RequestParam(required = false) String orderStatus,
                     @RequestParam(required = false) String paymentStatus){
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );


        Page<Order> orders;
        if (orderStatus != null && paymentStatus != null) {
            return ResponseEntity.ok(orderRepository.findByOrderStatusAndPaymentStatus(
                    orderStatus,paymentStatus, pageable));
        } else if (orderStatus != null) {
            return ResponseEntity.ok(orderRepository.findByOrderStatus(
                    orderStatus, pageable));
        } else if (paymentStatus != null) {
            return ResponseEntity.ok(orderRepository.findByPaymentStatus(
                    paymentStatus, pageable));
        } else {
            return ResponseEntity.ok(orderRepository.findAll(pageable));
        }

    }
    @GetMapping("api/viewOrders")
    public ResponseEntity<?> viewOrder(@RequestParam("orderId") String orderId){
      Optional<Order> orders=orderRepository.findByOrderId(orderId);
      return  ResponseEntity.ok(orders);

    }
    @GetMapping("api/orders/files")
    public ResponseEntity<Map<String, List<String>>> getOrderFiles(@RequestParam(defaultValue = "orderId") String orderId) {
       Optional<Order> order = orderRepository.findByOrderId(orderId);


        Map<String, List<String>> response = new HashMap<>();
        response.put("fileUrls", order.get().getFileUrls());
        response.put("filenames", order.get().getFilenames());

        return ResponseEntity.ok(response);
    }
}

