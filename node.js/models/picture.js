const mongoose = require('mongoose');

// Define Schemes
const gallerySchema = new mongoose.Schema({
  name: { type: String, required: true, unique: true },
},
{
  timestamps: true
});

const Picture = mongoose.model('Picture', gallerySchema);

const picture = new Picture({
    "name": "testentry"
});

// Create new todo document
gallerySchema.statics.create = function (payload) {
    // this === Model
    const Picture = new this(payload);
    // return Promise
    return Picture.save();
  };
  
  // Find All
  gallerySchema.statics.findAll = function () {
    // return promise
    // V4부터 exec() 필요없음
    return this.find({});
  };
  
  // Find One by todoid
  gallerySchema.statics.findOneByName = function (name) {
    return this.findOne({ name });
  };
  
  // Update by todoid
  gallerySchema.statics.updateByName = function (name, payload) {
    // { new: true }: return the modified document rather than the original. defaults to false
    return this.findOneAndUpdate({ name }, payload, { new: true });
  };
  
  // Delete by todoid
  gallerySchema.statics.deleteByName = function (name) {
    return this.remove({ name });
  };


// Create Model & Export
module.exports = Picture;