package fr.free.nrw.commons.explore.media

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.notNull
import fr.free.nrw.commons.Media
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.wikipedia.dataclient.mwapi.MwQueryPage
import org.wikipedia.gallery.ExtMetadata
import org.wikipedia.gallery.ImageInfo
import org.wikipedia.wikidata.Entities
import java.lang.IllegalArgumentException

class MediaConverterTest {
    @Mock
    lateinit var page: MwQueryPage
    @Mock
    lateinit var entity: Entities.Entity
    @Mock
    lateinit var imageInfo: ImageInfo
    @Mock
    lateinit var metadata: ExtMetadata

    lateinit var mediaConverter: MediaConverter
    lateinit var media: Media


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mediaConverter = MediaConverter()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConvertNoMetadata() {
        mediaConverter.convert(page, entity, imageInfo)
    }

    @Test
    fun testConvertIfThumbUrlBlank() {
        Mockito.`when`(imageInfo.metadata).thenReturn(metadata)
        Mockito.`when`(imageInfo.thumbUrl).thenReturn("")
        Mockito.`when`(imageInfo.originalUrl).thenReturn("originalUrl")
        Mockito.`when`(imageInfo.metadata?.licenseUrl()).thenReturn("licenseUrl")
        Mockito.`when`(imageInfo.metadata?.dateTime()).thenReturn("yyyy-MM-dd HH:mm:ss")
        media = mediaConverter.convert(page, entity, imageInfo)
        assertEquals(media.thumbUrl, media.imageUrl, "originalUrl")
    }

    @Test
    fun testConvertIfThumbUrlNotBlank() {
        Mockito.`when`(imageInfo.metadata).thenReturn(metadata)
        Mockito.`when`(imageInfo.thumbUrl).thenReturn("thumbUrl")
        Mockito.`when`(imageInfo.originalUrl).thenReturn("originalUrl")
        Mockito.`when`(imageInfo.metadata?.licenseUrl()).thenReturn("licenseUrl")
        Mockito.`when`(imageInfo.metadata?.dateTime()).thenReturn("yyyy-MM-dd HH:mm:ss")
        media = mediaConverter.convert(page, entity, imageInfo)
        assertEquals(media.thumbUrl, "thumbUrl")
    }

    @Test
    fun testConvertRemoveNotFoundDialogue() {
        Mockito.`when`(imageInfo.metadata).thenReturn(metadata)
        Mockito.`when`(imageInfo.thumbUrl).thenReturn("thumbUrl")
        Mockito.`when`(imageInfo.originalUrl).thenReturn("originalUrl")
        Mockito.`when`(imageInfo.metadata?.licenseUrl()).thenReturn("licenseUrl")
        Mockito.`when`(imageInfo.metadata?.dateTime()).thenReturn("yyyy-MM-dd HH:mm:ss")
        Mockito.`when`(imageInfo.metadata?.artist()).thenReturn(
            "<a href=\"someURL\" class=\"new\" " +
                    "title=\"User: Username (page does not exist)\">Username</a>")
        media = mediaConverter.convert(page, entity, imageInfo)
        assertEquals(media.creator, "Username")
    }
}