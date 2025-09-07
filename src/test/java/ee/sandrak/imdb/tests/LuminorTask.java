package ee.sandrak.imdb.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.Attachment;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;

import java.time.Duration;

@Listeners({io.qameta.allure.testng.AllureTestNg.class})

/*
Automate below test steps as you would automate a regression test case:

1. Open imdb.com
2. Search for "QA" with the search bar
3. When dropdown opens, save the name of the first title
4. Click on the first title
5. Verify that page title matches the one saved from the dropdown
6. Verify there are more than 3 members in the "top cast section"
7. Click on the 3rd profile in the "top cast section"
8. Verify that correct profile have opened
9 Added my extra step: Re-open saved title via DROPDOWN and verify the actor is in Top cast of selected movie
Use: Gradle, Selenide, java 17, TestNG, Allure-report
*/

public class LuminorTask {

    private String savedTitle;
    private String currentTitle;
    private String actorName;
    private SelenideElement firstTitleToClick; //“cache” lingi hoidmiseks sammu 4 jaoks

    @BeforeClass
    public void setup() {
        Configuration.baseUrl = "https://www.imdb.com";
        Configuration.browserSize = "2560x1440";
        Configuration.timeout = 8000;
        Configuration.clickViaJs = true;
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @Test(description = "Sandrak Luminor Home task in IMDB")
    @Description("Start from step 1 and go through all the way to step 9 baby!")
    public void openAndTypeQA() {
        step1_openImdbAndCloseCookies();                  // 1
        step2_searchForQA();                              // 2
        step3_saveFirstTitleFromDropdown();               // 3
        step4_clickFirstTitle();                          // 4
        step5_verifyH1EqualsSavedTitle();                 // 5
        step6_waitCastAndAssertMoreThan3();               // 6
        step7_click3rdProfile_saveName_clickPicture();    // 7
        step8_verifyCorrectProfileOpened();               // 8
        step9_reopenSavedTitleAndVerifyActorInTopCast();  // 9
    }
    
    
    /* ---------------------------- STEPS ---------------------------- */

        @Step("1) Open imdb.com")
        private void step1_openImdbAndCloseCookies() { 
            // 1 Open imdb.com
            open("/");
            closeCookiesIfVisible(); // 1.1) Sulgeb cookie banneri, kui peaks tulema
             }
        
        @Step("2) Search for \"QA\" with the search bar")
         private void step2_searchForQA() {
        // 2) Search for "QA" with the search bar
        SelenideElement search = $("input#suggestion-search, input[name='q']");
        search.shouldBe(visible).setValue("QA");
            }
        
        @Step("3) When dropdown opens, save the name of the first title (cleaned)")
        private void step3_saveFirstTitleFromDropdown() {
        // 3) When dropdown opens, save the name of the first title ("cleaned" version)
        SelenideElement firstTitleLink =
            $$("div[role='listbox'] a[href^='/title/']").first().shouldBe(visible);
        String dropdownText = firstTitleLink.getText();

        savedTitle = extractTitleFromDropdown(dropdownText);
        Allure.step("Saved first title (clean): " + savedTitle);
        //System.out.println("Saved first title from dropdown: " + savedTitle);

        $("div[role='listbox']").shouldBe(visible);

        firstTitleToClick = firstTitleLink; 
        }

        @Step("4) Click on the first title")
        private void step4_clickFirstTitle() {   
        // 4) Click on the first title
        firstTitleToClick.click();
        }

        @Step("5) Verify title page H1 equals the saved title")
        private void step5_verifyH1EqualsSavedTitle() {
        // 5) Verify title page H1 equals saved title
        String h1Text = $("h1[data-testid='hero__pageTitle'], h1").shouldBe(visible).getText();
        currentTitle = normalizeH1(h1Text); // normalizeH1 eemaldab tiitlis (sulgudes olevad sõnad) - see on siin vajalik, kuna H1 oleks muidu "Q&A (1990)" aga dropdownist võetud tiitel on "Q&A"
        Allure.step("H1 (normalized): " + currentTitle);
        attachSaved(savedTitle);
        attachH1(currentTitle);

        // Lisan, et ootaks, kuni "Top cast" plokk on olemas ja nähtav, kuna muidu võib test sammud liiga kiirelt joosta ja failida. 
        $("section[data-testid='title-cast']").shouldBe(visible, Duration.ofSeconds(10));
        // vahel tuleb lehel uus bänner/hüpik, paneb uuesti kinni
        closeCookiesIfVisible();

        // Attachments to Allure
        attachSaved(savedTitle); // Puhas tiitel mis on dropdownis "QA" mitte,"Q&A 1990 Nick Nolte, Timothy Hutton"
        attachH1(currentTitle);
        }

        @Step("6) Verify there are more than 3 members in the Top cast section")
        private void step6_waitCastAndAssertMoreThan3() {
        // 6) Verify there are more than 3 members in the "top cast section"
        $$("section[data-testid='title-cast'] li").shouldHave(sizeGreaterThan(3));
        Assert.assertEquals(currentTitle, savedTitle,
                "Title page H1 must equal the saved dropdown title");

        }

        @Step("7) Click on the 3rd profile in the Top cast section (save name and click picture)")
         private void step7_click3rdProfile_saveName_clickPicture() {    
        // 7) Click on the 3rd profile in the "top cast section" - see oli hea peavalu, kuna imdb peideb elemente või enda sisselugemise aeg on ebaühtlane. 
        //Ma alguses proovisin nime klikiga, kuid lõpuks läksin pildi vajutusega. Kuigi see ka vahel failib ja vahel passib
        closeCookiesIfVisible(); // kui banner teki(b)
        $("section[data-testid='title-cast']").shouldBe(visible);

        // 7.1 — salvesta nimi Allure'i
        ElementsCollection names =
                $$("section[data-testid='title-cast'] a[data-testid='title-cast-item__actor']");
        names.shouldHave(sizeGreaterThan(2));
        actorName = names.get(2).getText().trim();
        attachProfile3(actorName);
        // System.out.println("Third name in the top cast list is " + actorName);


        // 7.2 — kliki sama indeksiga pildil (klikitav ankur, mitte overlay-div)
        ElementsCollection pictureLinks =
                $$("section[data-testid='title-cast'] a.ipc-lockup-overlay");  // <-- ANCHOR!
        pictureLinks.shouldHave(sizeGreaterThan(2));

        SelenideElement thirdPicLink = pictureLinks.get(2);
        thirdPicLink.scrollTo().shouldBe(visible).click();   // tavaline klikk
        }

        @Step("8) Verify the correct profile page opened")
        private void step8_verifyCorrectProfileOpened() {
        // 8) Verify correct profile page opened - siin ma ei olnud kindel, et kas vaja oli, et kontrolliks, et kas actor name ja h1 kattuvad või mis. Tegin lisaks ise juurde kontroll 9. 
        String personH1 = $("h1[data-testid='hero__pageTitle'], h1")
                .shouldBe(visible, Duration.ofSeconds(10))
                .getText().trim();

        // logi ja lisa Allure’i
        // System.out.println("Opened profile H1: " + personH1);
        Allure.step("Opened profile H1: " + personH1);

        Assert.assertTrue(
                personH1.contains(actorName),
                "Expected profile H1 to contain: " + actorName + " but was: " + personH1
        );
        }

        @Step("9) Re-open saved title via dropdown and verify the actor is listed in Top cast")
        private void step9_reopenSavedTitleAndVerifyActorInTopCast() {
        // 9) Re-open saved title via DROPDOWN and verify the actor is in Top cast
        open("/");
        closeCookiesIfVisible();

        SelenideElement search2 = $("input#suggestion-search, input[name='q']").shouldBe(visible);
        search2.clear();
        search2.setValue(savedTitle);

        // dropdown avaneb
        $("div[role='listbox']").should(appear);

        // vali õigesti sama pealkiri dropdownist (link, mitte Enter)
        $$("div[role='listbox'] a[href^='/title/']")
                .findBy(text(savedTitle))
                .shouldBe(visible)
                .click();

        // leht lahti
        $("h1[data-testid='hero__pageTitle'], h1").shouldBe(visible);

        // kontroll: Top cast sisaldab 3. näitlejat
        $$("section[data-testid='title-cast'] a[data-testid='title-cast-item__actor']")
                .findBy(text(actorName))
                .shouldBe(visible);

        Allure.step("Verified that '" + actorName + "' appears in Top cast of '" + savedTitle + "'");


    }

    /* ---------- helpers ---------- */

    // Sulgeb cookie banneri(d) jms
    private void closeCookiesIfVisible() {
        String[] selectors = new String[]{
            "button#accept-choices",
            "button#reject-choices",
            "button[data-testid='accept-button']",
            "button[data-testid='reject-button']",
            "button[aria-label='Accept all']",
            "button[aria-label='Decline all']"
        };
        for (String s : selectors) {
            try {
                SelenideElement btn = $(s);
                if (btn.exists() && btn.isDisplayed()) { btn.click(); break; }
            } catch (Throwable ignored) {}
        }
    }

    // Nime otsingu dropdown joaks -> salvestab ainult filmi tiitlie, mitte kõik lisa (näitlejad, aasta, jms.)
    private String extractTitleFromDropdown(String raw) {
        if (raw == null) return "";
        String firstLine = raw.split("\\R")[0].trim();    
        firstLine = firstLine.replaceAll("\\s+(?:19|20)\\d{2}.*$", ""); 
        firstLine = firstLine.replaceAll("\\s*\\(\\d{4}.*\\)$", "");    
        return firstLine.trim();
    }

    // normalizeH1 eemaldab tiitlis (sulgudes olevad sõnad)
    private String normalizeH1(String raw) {
        if (raw == null) return "";
        String firstLine = raw.split("\\R")[0].trim();
        return firstLine.replaceAll("\\s*\\(.*\\)$", "").trim();
    }
    

    @Attachment(value = "Saved dropdown title", type = "text/plain") // Puhas tiitel mis on dropdownis "QA" mitte,"Q&A 1990 Nick Nolte, Timothy Hutton"
    private String attachSaved(String t) { return t; }

    @Attachment(value = "Opened H1 title", type = "text/plain") //
    private String attachH1(String t) { return t; }

    @Attachment(value = "Opened 3rd profile title", type = "text/plain") //
    private String attachProfile3(String t) { return t; }
}