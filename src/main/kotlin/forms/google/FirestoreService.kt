package forms.google

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import forms.model.Consignment
import forms.model.Order
import forms.model.User
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService constructor(@Inject var credentials: GoogleCredentials) {
    private val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId("forms-304923")
            .setCredentials(credentials)
            .build();
    val db: Firestore = firestoreOptions.service;


    fun upsertUser(user: User) : User {
        if (user.id.isEmpty()) {
            user.id = UUID.randomUUID().toString();
        }
        db.collection("users").document(user.id).set(user).get()
        println("Saved user ${user.id}")
        return user
    }

    fun getUsers( ): List<User> {
        val query = db.collection("users").get();
        return query.get(30, TimeUnit.SECONDS).documents.map { it.toObject(User::class.java) }
    }

    fun getUserById(id: String): User? {
        return db.collection("users").document(id).get().get().toObject(User::class.java)
    }

    fun upsertOrder(order: Order) : Order {
        if (order.orderId.isNullOrBlank()) {
            val orderId = UUID.randomUUID().toString();
            order.orderId = orderId;
        }
        db.collection("orders").document(order.orderId!!).set(order).get()
        return order
    }

    fun getOrders(user: String) : List<Order> {
        return db.collection("orders")
            .whereEqualTo("creator", user)
            .get().get().documents
            .map { it.toObject(Order::class.java) }
    }

    fun getOrderById(id: String): Order? {
        return db.collection("orders").document(id).get().get().toObject(Order::class.java)
    }

    fun upsertConsignment(consigment: Consignment): Consignment {
        if (consigment.id.isNullOrBlank()) {
            consigment.id = UUID.randomUUID().toString();
        }
        db.collection("consigments").document(consigment.id!!).set(consigment).get()
        return consigment
    }

    fun getConsigmentsForUser(user: String): List<Consignment> {
        return db.collection("consigments").whereEqualTo("issuedTo", user)
                .get().get().documents.map { it.toObject(Consignment::class.java) }
    }

    fun getConsigmentById(id: String): Consignment? {
        return db.collection("consigments").document(id).get().get().toObject(Consignment::class.java)
    }
}