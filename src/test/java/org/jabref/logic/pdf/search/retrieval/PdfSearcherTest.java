package org.jabref.logic.pdf.search.retrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import org.jabref.logic.pdf.search.indexing.PdfIndexer;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.LinkedFile;
import org.jabref.model.pdf.search.PdfSearchResults;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdfSearcherTest {

    private PdfSearcher search;

    @Before
    public void setUp() throws IOException {
        search = new PdfSearcher();

        // given
        PdfIndexer indexer = new PdfIndexer();
        BibDatabase database = new BibDatabase();
        BibDatabaseContext context = mock(BibDatabaseContext.class);
        when(context.getFileDirectoriesAsPaths(Mockito.any())).thenReturn(Collections.singletonList(Paths.get("src/test/resources/pdfs")));
        BibEntry examplePdf = new BibEntry("article");
        examplePdf.setFiles(Collections.singletonList(new LinkedFile("Example Entry", "example.pdf", "pdf")));
        database.insertEntry(examplePdf);

        BibEntry metaDataEntry = new BibEntry("article");
        metaDataEntry.setFiles(Collections.singletonList(new LinkedFile("Metadata Entry", "metaData.pdf", "pdf")));
        metaDataEntry.setCiteKey("MetaData2017");
        database.insertEntry(metaDataEntry);

        BibEntry exampleThesis = new BibEntry("PHDThesis");
        exampleThesis.setFiles(Collections.singletonList(new LinkedFile("Example Thesis", "thesis-example.pdf", "pdf")));
        exampleThesis.setCiteKey("ExampleThesis");
        database.insertEntry(exampleThesis);

        indexer.createIndex(database, context);
    }


    @Test
    public void searchForTest() throws IOException, ParseException {
        PdfSearchResults result = search.search("test", 10);
        assertEquals(2, result.numSearchResults());
    }

    @Test
    public void searchForUniversity() throws IOException, ParseException {
        PdfSearchResults result = search.search("University", 10);
        assertEquals(1, result.numSearchResults());
    }

    @Test
    public void searchForStopWord() throws IOException, ParseException {
        PdfSearchResults result = search.search("and", 10);
        assertEquals(0, result.numSearchResults());
    }

    @Test
    public void searchForSecond() throws IOException, ParseException {
        PdfSearchResults result = search.search("second", 10);
        assertEquals(2, result.numSearchResults());
    }

    @Test
    public void searchForEmptyString() throws IOException {
        PdfSearchResults result = search.search("", 10);
        assertEquals(0, result.numSearchResults());
    }

    @Test(expected = NullPointerException.class)
    public void searchWithNullString() throws IOException {
        search.search(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchForZeroResults() throws IOException {
        search.search("test", 0);
    }
}