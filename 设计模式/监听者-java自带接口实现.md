

### Weather

```java
package com.paincupid.springmvc.listenerpattern;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public interface Weather {
    void weatherContent();
}
```



### CloudDay

```java
package com.paincupid.springmvc.listenerpattern;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public class CloudDay implements Weather {
    private int temperature;
    private int humidity;

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override public void weatherContent() {
        System.out.println("weatherContent: humidity" + humidity + ", temperature" + temperature);
    }
}
```



### RainingDay

```java
package com.paincupid.springmvc.listenerpattern;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public class RainingDay implements Weather {
    private int waterMilliliter;//降水量
    private int temperature;

    public int getWaterMilliliter() {
        return waterMilliliter;
    }

    public void setWaterMilliliter(int waterMilliliter) {
        this.waterMilliliter = waterMilliliter;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override public void weatherContent() {
        System.out.println("weatherContent: waterMilliliter" + waterMilliliter + ", temperature" + temperature);
    }
}
```



### ReaderObserver1

```java
package com.paincupid.springmvc.listenerpattern;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public class ReaderObserver1 implements Observer {

    private WeatherReport weatherReport;

    @Override public void update(Observable o, Object arg) {

        weatherReport = (WeatherReport) o;

        if (((WeatherReport) o).getWeatherContent() instanceof RainingDay) {
            RainingDay rainingDay = (RainingDay) weatherReport.getWeatherContent();
            System.out.println(
                    "ReaderObserver1 book raining day, raining Day = " + rainingDay.getTemperature() + ", " + rainingDay
                            .getWaterMilliliter() + ", arg = " + arg);
        }

    }
}
```



### ReaderObserver2

```java
package com.paincupid.springmvc.listenerpattern;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public class ReaderObserver2 implements Observer {
    private WeatherReport weatherReport;

    @Override public void update(Observable o, Object arg) {
        weatherReport = (WeatherReport) o;

        if (((WeatherReport) o).getWeatherContent() instanceof CloudDay) {
            CloudDay cloudDay = (CloudDay) weatherReport.getWeatherContent();
            System.out.println(
                    "ReaderObserver2 book cloud day, cloud Day = " + cloudDay.getTemperature() + ", " + cloudDay
                            .getHumidity() + ", arg = " + arg);
        }
    }
}
```



### WeatherReport

```java
package com.paincupid.springmvc.listenerpattern;

import java.util.Observable;

/**
 * Created by arthur.dy.lee on 2018/8/22.
 */
public class WeatherReport extends Observable {

    private Weather weatherContent;

    public Weather getWeatherContent() {
        return weatherContent;
    }

    public void setWeatherContent(Weather weatherContent) {
        this.weatherContent = weatherContent;
        setChanged();
        //notifyObservers();
        notifyObservers("arthur");
    }

    public static void main(String[] args) {
        WeatherReport weatherReport = new WeatherReport();
        ReaderObserver1 readerObserver1 = new ReaderObserver1();
        ReaderObserver2 readerObserver2 = new ReaderObserver2();
        weatherReport.addObserver(readerObserver1);
        weatherReport.addObserver(readerObserver2);

        RainingDay rainingDay = new RainingDay();
        rainingDay.setTemperature(10);
        rainingDay.setWaterMilliliter(1);

        CloudDay cloudDay = new CloudDay();
        cloudDay.setHumidity(80);
        cloudDay.setTemperature(20);

        weatherReport.setWeatherContent(rainingDay);
        weatherReport.setWeatherContent(cloudDay);

    }

}
```



### Result

```txt
ReaderObserver1 book raining day, raining Day = 10, 1, arg = arthur
ReaderObserver2 book cloud day, cloud Day = 20, 80, arg = arthur
```

