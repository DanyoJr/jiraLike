package com.jiralike.android.data.network

import com.jiralike.android.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface JiraApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    @GET("projects")
    suspend fun getProjects(@Header("Authorization") token: String): Response<List<ProjectDto>>

    @POST("projects")
    suspend fun createProject(
        @Header("Authorization") token: String,
        @Body request: CreateProjectRequest
    ): Response<ProjectDto>

    @GET("projects/{projectId}/issues")
    suspend fun getIssues(
        @Header("Authorization") token: String,
        @Path("projectId") projectId: Long
    ): Response<List<IssueDto>>

    @GET("projects/{projectId}")
    suspend fun getProjectDetails(
        @Header("Authorization") token: String,
        @Path("projectId") projectId: Long
    ): Response<com.jiralike.android.data.model.ProjectDetailDto>

    @POST("projects/{projectId}/issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Path("projectId") projectId: Long,
        @Body request: CreateIssueRequest
    ): Response<IssueDto>

    @PATCH("issues/{id}/status")
    suspend fun updateIssueStatus(
        @Header("Authorization") token: String,
        @Path("id") issueId: Long,
        @Body request: UpdateStatusRequest
    ): Response<IssueDto>
}
