package org.tw;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ToDoAppSelenium {
    private static ChromeDriver driver;
    private static JavascriptExecutor js;

    public static void main(String[] args) {
        setupEnvironment();
        navigateToApp();
        clearAndSetLocalStorage();
        accessJavascriptVariables();
//        waitPlease(3);
//        handleDisabledInput("Disabled Input Task");

//        waitPlease(3);
//        // fist task pointer-events is none so user can't click it so selenium also can't
//        forceClickThroughCSSBarrier();
//        adjustPageZoomForResponsiveTesting();
//        countCompletedTasksByStyle();
//        waitPlease(2);
//        scrollElementIntoView();
//        waitPlease(2);
//        interactWithElementAfterADelay("to-do-item-41");
//        waitPlease(2);
//        toggleElementVisibility();
        cleanup();
    }

    private static void accessJavascriptVariables() {
        // Accessing JavaScript variables
        String apiUrl = (String) js.executeScript("return appConfig.apiUrl;");
        System.out.println("API URL: " + apiUrl);

    }

    private static void setupEnvironment() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\siva0\\Downloads\\chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
    }

    private static void navigateToApp() {
        driver.get("http://localhost:3000");
    }

    private static void clearAndSetLocalStorage() {
        // Clear all tasks stored in local storage before running a test
        // This demonstrates manipulating browser storage, which Selenium cannot do directly
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.localStorage.setItem('task', 'Added via JS');");
        System.out.println("Local storage has been manipulated using JavaScript Executor.");
    }

    private static void handleDisabledInput(String taskName) {
        // Handle cases where inputs may be disabled due to app logic, demonstrating JS Executor's ability to modify DOM properties.
        WebElement input = driver.findElement(By.name("task"));
        WebElement submitButton = driver.findElement(By.id("taskform-submit"));

        try {
            input.sendKeys(taskName);
            submitButton.click();
        } catch (Exception e) {
            System.out.println("Input or submit button was disabled. Enabling and retrying via JavaScript.");
            js.executeScript("arguments[0].disabled = false; arguments[1].disabled = false;", input, submitButton);
//            difference between direct DOM manipulation and user-initiated events

//            When you directly manipulate the value of an HTML input element via JavascriptExecutor in Selenium,
//            the onChange event won't be triggered. This is because the onChange event in HTML forms is designed to
//            fire in response to user interactions like typing into a field, selecting a file in a file input,
//            or changing the selected option in a select element. Manipulating the element's value programmatically
//            does not simulate these user interactions.

//            This input approach can bypass any React-specific event handling tied to the onChange event,
//            If for some reason you must set the value via JavaScript
//            (e.g., to bypass input restrictions or to set values faster in bulk),
//            you can manually trigger the event using JavaScript

//            js.executeScript(
//                            "arguments[0].value = 'Override Task';" +
//                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
//                            "arguments[1].click();", input, submitButton
//            );

//            This input approach uses Selenium's standard methods to interact with the page,
//            which is more likely to trigger React's handling of state changes correctly
//            because it simulates actual user interaction more faithfully than direct DOM manipulation.
            input.sendKeys(taskName);
            waitPlease(2);
            js.executeScript("arguments[0].click();", submitButton);
        }
    }

    private static void forceClickThroughCSSBarrier() {
        // Force interactions with elements that are unclickable due to CSS properties like 'pointer-events: none'.
        try {
            WebElement firstTask = driver.findElement(By.id("to-do-item-0"));
            // Attempt to click may fail if covered by an overlay or styled with pointer-events: none
            firstTask.click();
        } catch (Exception e) {
            System.out.println("Element was not interactable. Forcing interaction through JavaScript.");
            js.executeScript(
                         "document.getElementById('to-do-item-0').style.pointerEvents='auto'; ");
            waitPlease(2);

            js.executeScript(
                            "document.getElementById('to-do-item-0').click();");
        }
    }

    private static void adjustPageZoomForResponsiveTesting() {
        // Adjust the browser zoom to simulate different screen sizes, useful for responsive design testing.
        js.executeScript("document.body.style.zoom='110%';");
    }

    private static void countCompletedTasksByStyle() {
        // Use JavaScript to access computed styles to verify UI elements are displaying as expected.
        // Demonstrate accessing and modifying element styles to verify specific UI conditions
        // Count elements with a specific background color that indicates completion
        Long completedCount = (Long) js.executeScript(
                "var items = document.getElementsByClassName('toDoItem');" +
                        "var count = 0;" +
                        "for (var i = 0; i < items.length; i++) {" +
                        "  var style = window.getComputedStyle(items[i]);" +
                        "  if (style.backgroundColor === 'rgb(128, 128, 128)') {" + // Grey in RGB
                        "    count++;" +
                        "  }" +
                        "}" +
                        "return count;"
        );
        System.out.println("Number of completed tasks with grey background: " + completedCount);
    }

    private static void scrollElementIntoView() {
        // Scroll an element into view using JavaScript, useful for elements that are out of the viewport.
        // Find the taskform element
        WebElement taskformElement = driver.findElement(By.id("taskform"));

        // Get the element's position
        int elementPosition = taskformElement.getLocation().getY();

        // Define the current position and step size
        int currentPosition = 0;
        int stepSize = 50;

        // Gradually scroll to the element
        while (currentPosition < elementPosition) {
            currentPosition += stepSize;
            String script = "window.scrollTo(0, " + currentPosition + ");";
            js.executeScript(script);
        }

        // Ensure the element is fully in view
        js.executeScript("arguments[0].scrollIntoView(true);", taskformElement);
    }

    private static  void interactWithElementAfterADelay(String newlyAddedElementId){
        handleDisabledInput("Checking Delay of task");
        // React app had a delay of 2 seconds to add a task, so it only appears after 2 seconds So we can use
        // JavaScript Executor to interact with an element after a 3-second delay
        try {
            WebElement newlyAddedTask = driver.findElement(By.id(newlyAddedElementId));
            newlyAddedTask.click(); // This will fail because the element is not visible in the viewport.
        } catch (Exception e) {
            System.out.println("Handled by Selenium Error as newTask will not appear in view immendiately we have to wait: " + e.getClass() + e.getMessage());
            String script = String.format("setTimeout(function() { document.getElementById('%s').click(); }, 3000);", newlyAddedElementId);
            js.executeScript(script);
        }
    }


    private static void toggleElementVisibility() {
        // Manage elements with conditional visibility, using JavaScript to ensure they are visible and interactable.
        //  If a button is not easily clickable due to styling issues or overlays:
        WebElement hiddenToogleButton = driver.findElement(By.id("toogle-visibility-button"));
        try {
            hiddenToogleButton.click();

        }catch (Exception e){
            System.out.println("Can't Click hidden element as it is hidden due to style display as none: " + e.getClass() + e.getMessage());
            System.out.println("Force Change styles and click using JavaScript for testing purpose.");
            js.executeScript(
                         "arguments[0].style.display='block'; "
                    , hiddenToogleButton);
            waitPlease(2);
            js.executeScript(
                            "arguments[0].click();"
                    , hiddenToogleButton);
        }
    }

    private static void cleanup() {
        // Allow time to observe the changes before closing the browser.
        waitPlease(10);  // pause for 10 seconds
        driver.quit();
    }

    private static void waitPlease(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
