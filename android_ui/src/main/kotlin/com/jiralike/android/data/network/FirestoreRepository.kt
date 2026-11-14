package com.jiralike.android.data.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jiralike.android.data.model.*
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersRef = db.collection("users")
    private val projectsRef = db.collection("projects")
    private val issuesRef = db.collection("issues")

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val querySnapshot = usersRef
                .whereEqualTo("email", request.email)
                .whereEqualTo("password", request.password)
                .get().await()

            if (!querySnapshot.isEmpty) {
                val user = querySnapshot.documents[0].toObject(UserDto::class.java)
                if (user != null) {
                    Result.success(AuthResponse(accessToken = user.id, email = user.email, name = user.name))
                } else {
                    Result.failure(Exception("Failed to parse user."))
                }
            } else {
                Result.failure(Exception("Invalid email or password."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<UserDto> {
        return try {
            val newUser = UserDto(name = request.name, email = request.email, password = request.password)
            val docRef = usersRef.add(newUser).await()
            val userWithId = newUser.copy(id = docRef.id)
            Result.success(userWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProjects(userEmail: String): Result<List<ProjectDto>> {
        return try {
            val snapshot = projectsRef.whereEqualTo("ownerEmail", userEmail).get().await()
            val projects = snapshot.toObjects(ProjectDto::class.java)
            Result.success(projects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProject(request: CreateProjectRequest, ownerEmail: String): Result<ProjectDto> {
        return try {
            val project = ProjectDto(
                name = request.name,
                key = request.key,
                description = request.description,
                ownerEmail = ownerEmail
            )
            val docRef = projectsRef.add(project).await()
            project.id = docRef.id
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIssues(projectId: String): Result<List<IssueDto>> {
        return try {
            val snapshot = issuesRef.whereEqualTo("projectId", projectId).get().await()
            val issues = snapshot.toObjects(IssueDto::class.java)
            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createIssue(projectId: String, request: CreateIssueRequest): Result<IssueDto> {
        return try {
            val issue = IssueDto(
                projectId = projectId,
                title = request.title,
                description = request.description,
                type = request.type,
                priority = request.priority
            )
            val docRef = issuesRef.add(issue).await()
            issue.id = docRef.id
            Result.success(issue)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIssueStatus(issueId: String, request: UpdateStatusRequest): Result<IssueDto> {
        return try {
            issuesRef.document(issueId).update("status", request.status).await()
            val doc = issuesRef.document(issueId).get().await()
            val issue = doc.toObject(IssueDto::class.java)
            if (issue != null) {
                Result.success(issue)
            } else {
                Result.failure(Exception("Failed to load updated issue."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
