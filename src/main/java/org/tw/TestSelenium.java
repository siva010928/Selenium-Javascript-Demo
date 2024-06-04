package org.tw;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class TestSelenium {
    public static void main(String[] args){

        // Setup the ChromeDriver path
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\siva0\\Downloads\\chromedriver\\chromedriver.exe");

        // Initialize ChromeDriver
        ChromeDriver driver = new ChromeDriver();

        // Navigate to the To-Do App
        driver.get("http://localhost:3002");

        // Create a JavascriptExecutor instance from the driver
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Example 1

        // Clear all tasks stored in local storage before running a test
        // This demonstrates manipulating browser storage, which Selenium cannot do directly
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.localStorage.setItem('task', 'Task from JS Executor');");

        // Example 2

//        // Try to interact with an element that might be disabled due to application state
//        WebElement input = driver.findElement(By.name("task"));
//        WebElement submitButton = driver.findElement(By.id("taskform-submit"));
//
//        try {
//            // This attempt will fail if the input is disabled
//            input.sendKeys("New Task");
//            submitButton.click();
//        } catch (Exception e) {
//            System.out.println("Failed to send keys or click: " + e.getMessage());
//
//            js.executeScript("arguments[0].disabled = false; arguments[1].disabled = false;", input, submitButton);
//
////            difference between direct DOM manipulation and user-initiated events
//
////            When you directly manipulate the value of an HTML input element via JavascriptExecutor in Selenium,
////            the onChange event won't be triggered. This is because the onChange event in HTML forms is designed to
////            fire in response to user interactions like typing into a field, selecting a file in a file input,
////            or changing the selected option in a select element. Manipulating the element's value programmatically
////            does not simulate these user interactions.
//
////            This approach can bypass any React-specific event handling tied to the onChange event,
////            If for some reason you must set the value via JavaScript
////            (e.g., to bypass input restrictions or to set values faster in bulk),
////            you can manually trigger the event using JavaScript
//
////            js.executeScript(
////                            "arguments[0].value = 'Override Task';" +
////                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
////                            "arguments[1].click();", input, submitButton
////            );
//
////            This approach uses Selenium's standard methods to interact with the page,
////            which is more likely to trigger React's handling of state changes correctly
////            because it simulates actual user interaction more faithfully than direct DOM manipulation.
//            input.sendKeys("Override Task");
//            js.executeScript("arguments[0].click();", submitButton);
//        }


        // Example 3

        // Handle elements with CSS that makes them non-interactable, such as pointer-events set to none
        try {
            WebElement firstTask = driver.findElement(By.id("to-do-item-0"));
            // Attempt to click may fail if covered by an overlay or styled with pointer-events: none
            firstTask.click();
        } catch (Exception e) {
            System.out.println("Click intercepted, trying JS click: " + e.getMessage());
            // Use JS to force click
            js.executeScript(
                    "document.getElementById('to-do-item-0').style.pointerEvents='auto'; " +
                            "document.getElementById('to-do-item-0').click();");
        }


        // Example 4

        // Adjust the page zoom to test responsive design capabilities via JS
        js.executeScript("document.body.style.zoom='110%'");

        // Example 5

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



        // Example 6

        // Find the taskform element
        WebElement taskformElement = driver.findElement(By.id("taskform"));

        // Get the element's position
        int elementPosition = taskformElement.getLocation().getY();
        System.out.println(elementPosition);

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


        // Example 7

        // Try to interact with an element that might be disabled due to application state
        WebElement input = driver.findElement(By.name("task"));
        WebElement submitButton = driver.findElement(By.id("taskform-submit"));

        try {
            // This attempt will fail if the input is disabled
            input.sendKeys("New Task");
            submitButton.click();
        } catch (Exception e) {
            System.out.println("Failed to send keys or click: " + e.getMessage());

            js.executeScript("arguments[0].disabled = false; arguments[1].disabled = false;", input, submitButton);
            input.sendKeys("Override Task");
            js.executeScript("arguments[0].click();", submitButton);
        }

        // JavaScript Executor to interact with an element after a 3-second delay
        String newlyAddedElementId = "to-do-item-44";
        try {
            WebElement newlyAddedTask = driver.findElement(By.id(newlyAddedElementId));
            newlyAddedTask.click(); // This will fail because the element is not visible in the viewport.
        } catch (Exception e) {
            System.out.println("Handled by Selenium Error as newTask will not appear in view immendiately we have to wait: " + e.getClass() + e.getMessage());
            String script = String.format("setTimeout(function() { document.getElementById('%s').click(); }, 3000);", newlyAddedElementId);
            js.executeScript(script);
        }



        // Example 8


        //  Force Clicking a Button
        //  If a button is not easily clickable due to styling issues or overlays:
        // Find the add task button
        WebElement hiddenToogleButton = driver.findElement(By.id("toogle-visibility-button"));

        try {
            hiddenToogleButton.click();

        }catch (Exception e){
            System.out.println("Can't Click hidden element as it is hidden due to style display as none: " + e.getClass() + e.getMessage());
            // Force Change styles and click using JavaScript for testing purpose.
            js.executeScript(
                         "arguments[0].style.display='block'; " +
                            "arguments[0].click();"
                    , hiddenToogleButton);
        }


        // Close the browser
        waitPlease(10);
        driver.quit();
    }

    public static void waitPlease(int seconds){
        try {
            // Pause
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
