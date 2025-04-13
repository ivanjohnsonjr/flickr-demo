package com.inter.flickrapp.core.repo

import com.inter.flickrapp.core.data.GetPhotoInfoResponse
import com.inter.flickrapp.core.data.Photo
import com.inter.flickrapp.core.data.PhotoInfo
import com.inter.flickrapp.core.data.PhotoInfo.Content
import com.inter.flickrapp.core.data.PhotoInfo.Owner
import com.inter.flickrapp.core.data.PhotosResponse
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * FlickRepository Unit test
 *
 */
class FlickrRepositoryUnitTest {
    private lateinit var flickrService: FlickrServiceApi
    private lateinit var repo: FlickrRepository
    @Before
    fun setup() {
        flickrService = mockk<FlickrServiceApi>()
        repo = FlickrRepositoryImpl(flickrService)
    }

    @Test
    fun `Test getRecentPhotos successful`() =  runTest {
        val page = 1

        val response = Response.success(PhotosResponse(
            stat = "ok",
            message = null,
            result = PhotosResponse.PhotosResult(
                page = 1,
                pages = 5,
                perPage = 5,
                total = 25,
                photos = listOf(
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = "")
                )
            )
        ))

        every { flickrService.getRecentPhotos(page).execute() } returns response

        val result = repo.getRecentPhotos(page).first()

        assertEquals("page", 1, result.page)
        assertEquals("pages", 5, result.pages)
        assertEquals("perPage", 5, result.perPage)
        assertEquals("total", 25, result.total)
        assertEquals("Photo count", 5, result.photos.size)
    }

    @Test
    fun `Test getRecentPhotos fail with thrown Error`() =  runTest {
        var errorThrown = false
        val page = 1

        val responseBody = "Bad Request".toResponseBody()
        val response = Response.error<PhotosResponse>(
            404,
            responseBody
        )

        every { flickrService.getRecentPhotos(page).execute() } returns response

        repo.getRecentPhotos(page)
            .catch {
                errorThrown = true
            }
            .collect {  }

        assertTrue("getRecentPhotos error", errorThrown)
    }

    @Test
    fun `Test searchPhotos successful`() =  runTest {
        val page = 1
        val query = "test"

        val response = Response.success(PhotosResponse(
            stat = "ok",
            message = null,
            result = PhotosResponse.PhotosResult(
                page = 1,
                pages = 5,
                perPage = 5,
                total = 25,
                photos = listOf(
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = ""),
                    Photo(id = "1", owner = "12" , secret = "1", server = "1", title = "")
                )
            )
        ))

        every { flickrService.searchPhotos(query, page).execute() } returns response

        val result = repo.searchPhotos(query, page).first()

        assertEquals("page", 1, result.page)
        assertEquals("pages", 5, result.pages)
        assertEquals("perPage", 5, result.perPage)
        assertEquals("total", 25, result.total)
        assertEquals("Photo count", 5, result.photos.size)
    }

    @Test
    fun `Test searchPhotos fail with thrown Error`() =  runTest {
        var errorThrown = false
        val page = 1
        val query = "test"

        val responseBody = "Bad Request".toResponseBody()
        val response = Response.error<PhotosResponse>(
            404,
            responseBody
        )

        every { flickrService.searchPhotos(query, page).execute() } returns response

        repo.searchPhotos(query, page)
            .catch { ex ->
                errorThrown = true
            }
            .collect {  }

        assertTrue("searchPhotos error", errorThrown)
    }

    @Test
    fun `Test getPhotoInfo successful`() =  runTest {
        val photoId = "7"
        val secret = "87"
        val photoInfo = PhotoInfo(
            id = "1",
            secret = "2",
            server = "1",
            owner = Owner(
                id = "1",
                username = "username",
                realName = "real name",
                location = "",
                iconServer = "2334",
                iconFarm = 34
            ),
            title = Content("title"),
            description = Content("description"),
            views = 1,
            comments = null
        )

        val response = Response.success(GetPhotoInfoResponse(
            stat = "ok",
            message = null,
            result = photoInfo
        ))

        every { flickrService.getPhotoInfo(photoId, secret).execute() } returns response

        val result = repo.getPhotoInfo(photoId, secret).first()

        assertEquals("photo Info failed", photoInfo, result)
    }

    @Test
    fun `Test getPhotoInfo fail with thrown Error`() =  runTest {
        var errorThrown = false
        val photoId = "7"
        val secret = "87"

        val responseBody = "Bad Request".toResponseBody()
        val response = Response.error<GetPhotoInfoResponse>(
            404,
            responseBody
        )

        every { flickrService.getPhotoInfo(photoId, secret).execute() } returns response

        repo.getPhotoInfo(photoId, secret)
            .catch { ex ->
                errorThrown = true
            }
            .collect {  }

        assertTrue("getPhotoInfo error", errorThrown)
    }

    @Test
    fun `Test getPhotoInfo fail with stat=fail`() =  runTest {
        var actualErrorMessage = ""

        val photoId = "7"
        val secret = "87"
        val expectedMessage = "Failed with message"

        val response = Response.success(GetPhotoInfoResponse(
            stat = "fail",
            message = expectedMessage,
            result = null
        ))

        every { flickrService.getPhotoInfo(photoId, secret).execute() } returns response

        repo.getPhotoInfo(photoId, secret)
            .catch { ex ->
                actualErrorMessage = ex.message!!
            }
            .collect {  }

        assertEquals("getPhotoInfo error", expectedMessage, actualErrorMessage)
    }

}