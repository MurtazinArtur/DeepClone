package newjob.deepclone;

import java.util.List;
import java.util.Objects;

class Man {
    private String name;
    private int age;
    private Sex sex;
    private List<String> favoriteBooks;

    public Man(String name, int age, Sex sex, List<String> favoriteBooks) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.favoriteBooks = favoriteBooks;
    }

    public Man() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<String> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Man)) return false;
        Man man = (Man) o;
        return getAge() == man.getAge() &&
                Objects.equals(getName(), man.getName()) &&
                getSex() == man.getSex() &&
                Objects.equals(getFavoriteBooks(), man.getFavoriteBooks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getSex(), getFavoriteBooks());
    }

    @Override
    public String toString() {
        return "Man{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", favoriteBooks=" + favoriteBooks +
                '}';
    }
}