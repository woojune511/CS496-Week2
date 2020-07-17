const mongoose = require('mongoose');

// Define Schemes
const gallerySchema = new mongoose.Schema({
  name: { type: Number, required: true, unique: true },
},
{
  timestamps: true
});

// Create new picture document
gallerySchema.statics.create = function (payload) {
    // this === Model
    const picture = new this(payload);
    // return Promise
    return picture.save();
};


// Create Model & Export
module.exports = mongoose.model('Picture', gallerySchema);