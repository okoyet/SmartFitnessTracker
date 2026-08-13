package week11.st991708650.smartfitnesstracker.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st991708650.smartfitnesstracker.data.model.DailyStats
import week11.st991708650.smartfitnesstracker.data.model.UserProfile
import week11.st991708650.smartfitnesstracker.data.model.Workout

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // --------------------------------------------------
    // USER PROFILE
    // --------------------------------------------------

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(profile.userId)
                .set(profile, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                trySend(snapshot?.toObject(UserProfile::class.java))
            }

        awaitClose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // DAILY STATS
    // --------------------------------------------------

    suspend fun saveDailyStats(stats: DailyStats): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(stats.userId)
                .collection("daily_stats")
                .document(stats.date)
                .set(stats)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDailyStats(userId: String): Flow<DailyStats?> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("daily_stats")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val stats = snapshot?.documents?.firstOrNull()
                    ?.toObject(DailyStats::class.java)

                trySend(stats)
            }

        awaitClose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // WORKOUT - CREATE
    // --------------------------------------------------

    suspend fun addWorkout(workout: Workout): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(workout.userId)
                .collection("workouts")
                .add(workout)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // WORKOUT - READ
    // --------------------------------------------------

    fun getWorkouts(userId: String): Flow<List<Workout>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val workouts = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Workout::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(workouts)
            }

        awaitClose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // WORKOUT - UPDATE
    // --------------------------------------------------

    suspend fun updateWorkout(workout: Workout): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(workout.userId)
                .collection("workouts")
                .document(workout.id)
                .set(workout)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // WORKOUT - DELETE
    // --------------------------------------------------

    suspend fun deleteWorkout(workout: Workout): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(workout.userId)
                .collection("workouts")
                .document(workout.id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // ACCOUNT DELETION
    // --------------------------------------------------


    suspend fun deleteAllUserData(userId: String): Result<Unit> {
        return try {
            val userDoc = firestore.collection("users").document(userId)

            val workouts = userDoc.collection("workouts").get().await()
            for (document in workouts.documents) {
                document.reference.delete().await()
            }

            val dailyStats = userDoc.collection("daily_stats").get().await()
            for (document in dailyStats.documents) {
                document.reference.delete().await()
            }

            userDoc.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
