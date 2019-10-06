package demo.couchbase.part2.domain;

public class User implements Identificable {

    private String id;
    private String firstName;
    private String lastName;
    private String job;
    private Integer age;

    public User() {
    }

    public User(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.job = builder.job;
        this.age = builder.age;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static class Builder {

        private String id;
        private String firstName;
        private String lastName;
        private String job;
        private Integer age;

        public static Builder newInstance() {
            return new Builder();
        }

        public User build() {
            return new User(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder job(String job) {
            this.job = job;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }
    }
}
